/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package core;

import datastructures.CustomQueue;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.Dispatcher;
import scheduler.Feedback;
import scheduler.PolicyType;
import scheduler.RoundRobin;
import scheduler.Scheduler;
import scheduler.SchedulingPolicy;

/**
 * OperatingSystem coordina las transiciones de estado de los procesos y administra
 * las colas de listos, bloqueados, finalizados y suspendidos, asegurando que la
 * capacidad de memoria principal se respete en todo momento.
 */
public class OperatingSystem {

    /** Logger para trazabilidad de cambios de estado. */
    private static final Logger LOGGER = Logger.getLogger(OperatingSystem.class.getName());
    /** Capacidad máxima predeterminada de procesos residentes en memoria principal. */
    private static final int DEFAULT_MAX_PROCESSES_IN_MEMORY = 25;
    /** Duración por defecto de un ciclo del reloj global en milisegundos. */
    private static final long DEFAULT_CYCLE_DURATION_MILLIS = 100L;

    /** Cola de procesos listos. */
    private final CustomQueue<ProcessControlBlock> readyQueue;
    /** Cola de procesos bloqueados. */
    private final CustomQueue<ProcessControlBlock> blockedQueue;
    /** Cola de procesos finalizados. */
    private final CustomQueue<ProcessControlBlock> finishedProcessesList;
    /** Cola de procesos listos pero suspendidos (swap out). */
    private final CustomQueue<ProcessControlBlock> readySuspendedQueue;
    /** Cola de procesos bloqueados y suspendidos (swap out). */
    private final CustomQueue<ProcessControlBlock> blockedSuspendedQueue;
    /** Cerradura que protege todas las transiciones de estado. */
    private final Object stateLock;
    /** Contador de procesos residentes actualmente en memoria principal. */
    private int processesInMemory;
    /** Límite configurado de procesos que pueden residir simultáneamente en memoria. */
    private int maxProcessesInMemory;
    /** Conteo acumulado de ciclos del reloj global. */
    private final AtomicLong globalClockCycle;
    /** Bandera que representa si el reloj global está activo. */
    private final AtomicBoolean clockRunning;
    /** Cerradura dedicada al ciclo de reloj para evitar arranques concurrentes. */
    private final Object clockLock;
    /** Duración de cada ciclo del reloj en milisegundos. */
    private volatile long cycleDurationMillis;
    /** Hilo que ejecuta el ciclo de reloj del sistema. */
    private Thread clockThread;
    /** Referencia a la CPU coordinada por el sistema operativo. */
    private CPU cpu;
    /** Estrategia de planificación activa. */
    private Scheduler scheduler;
    /** Componente despachador responsable de cargar procesos en CPU. */
    private Dispatcher dispatcher;
    /** Quantum utilizado cuando Round Robin es la política activa. */
    private int roundRobinQuantum;
    /** Quantums por nivel utilizados cuando Feedback está activo. */
    private int[] feedbackQuanta;
    private transient QueueListener queueListener;
    private transient CpuListener cpuListener;

    /**
     * Construye el sistema operativo con colas vacías y contador en cero.
     */
    public OperatingSystem() {
        this.readyQueue = new CustomQueue<>();
        this.blockedQueue = new CustomQueue<>();
        this.finishedProcessesList = new CustomQueue<>();
        this.readySuspendedQueue = new CustomQueue<>();
        this.blockedSuspendedQueue = new CustomQueue<>();
        this.stateLock = new Object();
        this.processesInMemory = 0;
        this.maxProcessesInMemory = DEFAULT_MAX_PROCESSES_IN_MEMORY;
        this.globalClockCycle = new AtomicLong(0L);
        this.clockRunning = new AtomicBoolean(false);
        this.clockLock = new Object();
        this.cycleDurationMillis = DEFAULT_CYCLE_DURATION_MILLIS;
        this.clockThread = null;
        this.cpu = null;
        this.scheduler = new Scheduler();
        this.dispatcher = new Dispatcher();
        this.roundRobinQuantum = RoundRobin.DEFAULT_QUANTUM;
        this.feedbackQuanta = CPU.defaultFeedbackQuanta();
    }

    /**
     * Mueve el proceso a la cola de listos; si proviene de suspendido o nuevo se garantiza
     * antes que exista espacio en memoria principal.
     * @param pcb bloque de control que debe ir a ready
     */
    public void moveToReady(ProcessControlBlock pcb) {
        updateProcessState(pcb, ProcessState.LISTO, readyQueue, "readyQueue");
        notifyQueueListener();
        notifyCpuListener();
    }

    /**
     * Mueve el proceso a la cola de bloqueados manteniendo la coherencia de memoria.
     * @param pcb bloque de control que debe ir a bloqueados
     */
    public void moveToBlocked(ProcessControlBlock pcb) {
        updateProcessState(pcb, ProcessState.BLOQUEADO, blockedQueue, "blockedQueue");
        notifyQueueListener();
        notifyCpuListener();
    }

    /**
     * Marca el proceso como terminado, registra su finalización y libera memoria para
     * reingresar procesos suspendidos si es posible.
     * @param pcb proceso cuya ejecución culminó
     */
    public void markAsFinished(ProcessControlBlock pcb) {
        Objects.requireNonNull(pcb, "El proceso no puede ser nulo");
        synchronized (stateLock) {
            ProcessState previousState = pcb.getProcessState();
            readyQueue.remove(pcb);
            blockedQueue.remove(pcb);
            readySuspendedQueue.remove(pcb);
            blockedSuspendedQueue.remove(pcb);
            adjustMemoryCounters(previousState, ProcessState.TERMINADO);
            pcb.setProcessState(ProcessState.TERMINADO);
            if (pcb.getCompletionTime() < 0) {
                pcb.markCompleted(System.currentTimeMillis());
            }
            finishedProcessesList.enqueue(pcb);
            logTransition(pcb, previousState, ProcessState.TERMINADO, "finishedProcessesList");
            if (isInMemoryState(previousState)) {
                restoreSuspendedProcessIfPossible();
            }
        }
        notifyQueueListener();
        notifyCpuListener();
    }

    /**
     * Completa una operación de I/O moviendo el proceso a ready. Si el proceso estaba suspendido,
     * se verifica la capacidad antes de reingresarlo a memoria.
     * @param pcb proceso que retorna de la operación de I/O
     */
    public void completeIo(ProcessControlBlock pcb) {
        Objects.requireNonNull(pcb, "El proceso no puede ser nulo");
        synchronized (stateLock) {
            boolean removed = blockedQueue.remove(pcb);
            if (!removed) {
                removed = blockedSuspendedQueue.remove(pcb);
            }
            if (!removed) {
                LOGGER.log(Level.WARNING, "No se encontró el proceso {0} en las colas de bloqueados", pcb.getProcessId());
            }
            updateProcessState(pcb, ProcessState.LISTO, readyQueue, "readyQueue");
        }
        notifyQueueListener();
        notifyCpuListener();
    }

    /**
     * Devuelve el siguiente proceso de la cola de listos sin retirarlo.
     * @return PCB en la cabeza de ready o null si está vacía
     */
    public ProcessControlBlock peekReady() {
        synchronized (stateLock) {
            return readyQueue.peek();
        }
    }

    /**
     * Devuelve el siguiente proceso de la cola de bloqueados sin retirarlo.
     * @return PCB en la cabeza de bloqueados o null
     */
    public ProcessControlBlock peekBlocked() {
        synchronized (stateLock) {
            return blockedQueue.peek();
        }
    }

    /**
     * Devuelve el siguiente proceso finalizado registrado.
     * @return PCB en la cola de finalizados o null
     */
    public ProcessControlBlock peekFinished() {
        synchronized (stateLock) {
            return finishedProcessesList.peek();
        }
    }

    /**
     * Retira el siguiente proceso listo, generalmente usado por el planificador.
     * @return PCB extraído de ready o null
     */
    public ProcessControlBlock dequeueReady() {
        synchronized (stateLock) {
            return readyQueue.dequeue();
        }
    }

    /**
     * Devuelve una instantánea de la cola de listos para análisis externos (SRTF).
     * @return arreglo con los procesos actualmente listos
     */
    public ProcessControlBlock[] getReadyQueueSnapshot() {
        synchronized (stateLock) {
            Object[] raw = readyQueue.getAllProcesses();
            ProcessControlBlock[] snapshot = new ProcessControlBlock[raw.length];
            for (int i = 0; i < raw.length; i++) {
                snapshot[i] = (ProcessControlBlock) raw[i];
            }
            return snapshot;
        }
    }

    /**
     * Elimina un proceso específico de la cola de listos.
     * @param pcb proceso a remover
     * @return true si se eliminó correctamente
     */
    public boolean removeFromReadyQueue(ProcessControlBlock pcb) {
        synchronized (stateLock) {
            return readyQueue.remove(pcb);
        }
    }

    /**
     * Número de procesos listos actualmente en memoria.
     * @return tamaño de la cola ready
     */
    public int readyQueueSize() {
        synchronized (stateLock) {
            return readyQueue.size();
        }
    }

    /**
     * Número de procesos bloqueados actualmente en memoria.
     * @return tamaño de la cola blocked
     */
    public int blockedQueueSize() {
        synchronized (stateLock) {
            return blockedQueue.size();
        }
    }

    /**
     * Número de procesos que ya finalizaron.
     * @return tamaño de la cola de finalizados
     */
    public int finishedQueueSize() {
        synchronized (stateLock) {
            return finishedProcessesList.size();
        }
    }
    
    /**
     * Número de procesos listos pero suspendidos (swapped out).
     * @return tamaño de la cola de listos suspendidos
     */
    public int readySuspendedQueueSize() {
        synchronized (stateLock) {
            return readySuspendedQueue.size();
        }
    }
    
    /**
     * Número de procesos bloqueados y suspendidos (swapped out).
     * @return tamaño de la cola de bloqueados suspendidos
     */
    public int blockedSuspendedQueueSize() {
        synchronized (stateLock) {
            return blockedSuspendedQueue.size();
        }
    }
    
    /**
     * Número de procesos actualmente en memoria principal.
     * @return contador de procesos residentes
     */
    public int getProcessesInMemory() {
        synchronized (stateLock) {
            return processesInMemory;
        }
    }

    /**
     * Registra la CPU que cooperará con el reloj global del sistema.
     * @param cpu instancia de CPU asociada
     */
    public void attachCpu(CPU cpu) {
        this.cpu = Objects.requireNonNull(cpu, "La CPU asociada no puede ser nula");
        this.cpu.setScheduler(scheduler);
        this.cpu.setTimeQuantum(roundRobinQuantum);
        this.cpu.setFeedbackQuanta(feedbackQuanta);
        notifyCpuListener();
    }

    /**
     * Sustituye la estrategia de planificación utilizada para elegir el siguiente proceso listo.
     * @param scheduler planificador que seleccionará procesos
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = Objects.requireNonNull(scheduler, "El planificador no puede ser nulo");
        if (cpu != null) {
            cpu.setScheduler(this.scheduler);
            cpu.setTimeQuantum(roundRobinQuantum);
            cpu.setFeedbackQuanta(feedbackQuanta);
        }
    }

    /**
     * Define el despachador responsable de cargar procesos en la CPU.
     * @param dispatcher componente despachador a utilizar
     */
    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = Objects.requireNonNull(dispatcher, "El despachador no puede ser nulo");
    }

    /**
     * Cambia la política de planificación activa inyectando una implementación concreta.
     * @param policy política que será aplicada desde el siguiente ciclo
     */
    public void setSchedulingPolicy(SchedulingPolicy policy) {
        Objects.requireNonNull(policy, "La política de planificación no puede ser nula");
        scheduler.setPolicy(policy);
        if (cpu != null && policy instanceof RoundRobin) {
            cpu.setTimeQuantum(roundRobinQuantum);
        }
        if (cpu != null && policy instanceof Feedback) {
            cpu.setFeedbackQuanta(feedbackQuanta);
        }
    }

    /**
     * Cambia la política activa utilizando uno de los tipos registrados.
     * @param policyType enumeración de la política deseada
     */
    public void setSchedulingPolicy(PolicyType policyType) {
        Objects.requireNonNull(policyType, "El tipo de política no puede ser nulo");
        scheduler.setPolicy(policyType);
        if (cpu != null && policyType == PolicyType.ROUND_ROBIN) {
            cpu.setTimeQuantum(roundRobinQuantum);
        }
        if (cpu != null && policyType == PolicyType.FEEDBACK) {
            cpu.setFeedbackQuanta(feedbackQuanta);
        }
    }

    /**
     * Expone el planificador actual para permitir configuraciones avanzadas.
     * @return instancia compartida del scheduler
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * Define el quantum que utilizará Round Robin en futuras expropiaciones.
     * @param quantum cantidad de ciclos a asignar a cada proceso
     */
    public void setRoundRobinQuantum(int quantum) {
        if (!RoundRobin.isSupportedQuantum(quantum)) {
            throw new IllegalArgumentException("Quantum inválido para Round Robin: " + quantum);
        }
        if (cpu != null) {
            cpu.setTimeQuantum(quantum);
        }
        roundRobinQuantum = quantum;
    }

    /**
     * Devuelve el quantum actual configurado para Round Robin.
     * @return quantum en ciclos
     */
    public int getRoundRobinQuantum() {
        return roundRobinQuantum;
    }

    /**
     * Configura los quantums por nivel utilizados por la política Feedback.
     * @param quanta arreglo de valores positivos por nivel
     */
    public void setFeedbackQuanta(int[] quanta) {
        Objects.requireNonNull(quanta, "Los quantums de Feedback no pueden ser nulos");
        if (quanta.length != feedbackQuanta.length) {
            throw new IllegalArgumentException("Se esperaban " + feedbackQuanta.length + " niveles para Feedback");
        }
        int[] sanitized = new int[quanta.length];
        for (int i = 0; i < quanta.length; i++) {
            int value = quanta[i];
            if (value <= 0) {
                throw new IllegalArgumentException("El quantum para el nivel " + i + " debe ser positivo");
            }
            sanitized[i] = value;
        }
        this.feedbackQuanta = sanitized;
        if (cpu != null) {
            cpu.setFeedbackQuanta(feedbackQuanta);
        }
    }

    /**
     * Devuelve una copia de los quantums configurados para Feedback.
     * @return arreglo con los quantums por nivel
     */
    public int[] getFeedbackQuanta() {
        return Arrays.copyOf(feedbackQuanta, feedbackQuanta.length);
    }

    public ProcessControlBlock createProcess(String nombre,
                                             int totalInstrucciones,
                                             boolean ioBound,
                                             int ioCycle,
                                             int ioDuration) {
        Objects.requireNonNull(nombre, "El nombre del proceso no puede ser nulo");
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del proceso no puede estar vacío");
        }
        if (totalInstrucciones <= 0) {
            throw new IllegalArgumentException("Las instrucciones deben ser mayores que cero");
        }
        ProcessControlBlock pcb = new ProcessControlBlock(nombre.trim());
        pcb.setTotalInstructions(totalInstrucciones);
        pcb.setIOBound(ioBound);
        if (ioBound) {
            if (ioCycle < 0) {
                ioCycle = 0;
            }
            if (ioCycle >= totalInstrucciones) {
                ioCycle = Math.max(0, totalInstrucciones - 1);
            }
            if (ioDuration <= 0) {
                ioDuration = 1;
            }
            pcb.setIoExceptionCycle(ioCycle);
            pcb.setIoDuration(ioDuration);
        } else {
            pcb.setIoExceptionCycle(-1);
            pcb.setIoDuration(0);
        }
        moveToReady(pcb);
        return pcb;
    }

    /**
     * Ajusta la duración de cada ciclo de reloj para controlar la velocidad de simulación.
     * @param cycleDurationMillis tiempo en milisegundos por ciclo (no negativo)
     */
    public void setCycleDurationMillis(long cycleDurationMillis) {
        if (cycleDurationMillis < 0L) {
            throw new IllegalArgumentException("La duración del ciclo no puede ser negativa");
        }
        this.cycleDurationMillis = cycleDurationMillis;
    }

    public long getCycleDurationMillis() {
        return cycleDurationMillis;
    }

    /**
     * Devuelve el límite configurado de procesos residentes en memoria principal.
     * @return cantidad máxima de procesos en memoria
     */
    public int getMaxProcessesInMemory() {
        synchronized (stateLock) {
            return maxProcessesInMemory;
        }
    }

    /**
     * Ajusta el límite de procesos residentes en memoria reubicando suspendidos si corresponde.
     * @param maxProcesses nuevo tope de procesos (mínimo 1)
     */
    public void setMaxProcessesInMemory(int maxProcesses) {
        if (maxProcesses < 1) {
            throw new IllegalArgumentException("El límite de procesos debe ser al menos 1");
        }
        synchronized (stateLock) {
            if (this.maxProcessesInMemory == maxProcesses) {
                return;
            }
            this.maxProcessesInMemory = maxProcesses;
            ensureCapacity();
        }
        notifyQueueListener();
        notifyCpuListener();
    }

    /**
     * Obtiene el número acumulado de ciclos globales ejecutados por el reloj.
     * @return contador de ciclos globales
     */
    public long getGlobalClockCycle() {
        return globalClockCycle.get();
    }

    /**
     * Indica si el hilo de reloj del sistema operativo está en ejecución.
     * @return true cuando el reloj está activo
     */
    public boolean isClockRunning() {
        return clockRunning.get();
    }

    /**
     * Inicia el hilo de reloj que coordina planificación, despacho y ejecución en CPU.
     * Mantiene la secuencia: planificador → despachador → CPU.executeCycle().
     */
    public void startSystemClock() {
        synchronized (clockLock) {
            if (clockRunning.get()) {
                return;
            }
            if (cpu == null) {
                throw new IllegalStateException("Se debe registrar una CPU antes de iniciar el reloj del sistema");
            }
            clockRunning.set(true);
            clockThread = new Thread(this::runClockLoop, "SystemClock-Thread");
            clockThread.setDaemon(true);
            clockThread.start();
        }
    }

    /**
     * Detiene el hilo de reloj de forma ordenada y espera su terminación.
     */
    public void stopSystemClock() {
        Thread threadToJoin;
        synchronized (clockLock) {
            if (!clockRunning.get()) {
                return;
            }
            clockRunning.set(false);
            threadToJoin = clockThread;
            if (threadToJoin != null) {
                threadToJoin.interrupt();
            }
        }
        if (threadToJoin != null) {
            try {
                threadToJoin.join(1000L);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.WARNING, "Interrupción mientras se esperaba el cierre del reloj", ex);
            }
        }
        synchronized (clockLock) {
            if (clockThread == threadToJoin) {
                clockThread = null;
            }
        }
    }

    /**
     * Restablece el estado del simulador deteniendo el reloj y limpiando todas las colas.
     */
    public void resetSimulation() {
        stopSystemClock();
        synchronized (stateLock) {
            readyQueue.clear();
            blockedQueue.clear();
            finishedProcessesList.clear();
            readySuspendedQueue.clear();
            blockedSuspendedQueue.clear();
            processesInMemory = 0;
            if (cpu != null) {
                cpu.releaseProcess();
            }
        }
        globalClockCycle.set(0L);
        notifyQueueListener();
        notifyCpuListener();
    }

    /**
     * Ejecuta el bucle principal del reloj global incrementando el contador y coordinando las etapas.
     */
    private void runClockLoop() {
        LOGGER.info("Reloj del sistema iniciado");
        while (clockRunning.get()) {
            long currentCycle = globalClockCycle.incrementAndGet();
            LOGGER.info(() -> String.format("Ciclo global #%d", currentCycle));
            try {
                executeCyclePipeline();
            } catch (RuntimeException ex) {
                LOGGER.log(Level.SEVERE, "Fallo durante la ejecución del ciclo " + currentCycle, ex);
            }

            if (!clockRunning.get()) {
                break;
            }

            try {
                long sleepTime = cycleDurationMillis;
                if (sleepTime > 0L) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOGGER.info("Reloj del sistema interrumpido - apagado ordenado solicitado");
                break;
            }
        }
        clockRunning.set(false);
        LOGGER.info("Reloj del sistema detenido");
    }

    /**
     * Ejecuta la secuencia planificador → despachador → CPU para un ciclo del reloj.
     */
    private void executeCyclePipeline() {
        ProcessControlBlock scheduledProcess = runSchedulerStep();
        runDispatcherStep(scheduledProcess);
        runCpuStep();
    }

    /**
     * Solicita al planificador el siguiente proceso listo para ejecutar.
     * @return proceso seleccionado o null si no hay candidatos
     */
    private ProcessControlBlock runSchedulerStep() {
        if (cpu == null || !cpu.isIdle()) {
            return null;
        }
        ProcessControlBlock candidate;
        synchronized (stateLock) {
            long currentCycle = globalClockCycle.get();
            if (cpu.getScheduler() != null) {
                candidate = cpu.selectNextProcess(readyQueue);
            } else if (scheduler != null) {
                candidate = scheduler.selectNextProcess(readyQueue, cpu.getCurrentProcess(), currentCycle);
            } else {
                candidate = readyQueue.dequeue();
            }
        }
        if (candidate != null) {
            notifyQueueListener();
            notifyCpuListener();
        }
        return candidate;
    }

    /**
     * Entrega el proceso seleccionado al despachador para su carga en CPU.
     * @param candidate proceso listo a despachar
     */
    private void runDispatcherStep(ProcessControlBlock candidate) {
        if (candidate == null || cpu == null) {
            return;
        }
        if (!cpu.isIdle()) {
            moveToReady(candidate);
            return;
        }
        if (dispatcher != null) {
            dispatcher.dispatch(candidate, cpu);
        } else {
            dispatchDefault(candidate);
        }
        notifyCpuListener();
    }

    /**
     * Ejecuta el ciclo de CPU y comprueba si el proceso actual ha finalizado.
     */
    private void runCpuStep() {
        if (cpu == null) {
            return;
        }
        cpu.executeCycle();
        finalizeProcessIfCompleted();
        notifyCpuListener();
    }

    /**
     * Carga el proceso en la CPU usando el comportamiento por defecto cuando no hay despachador externo.
     * @param candidate proceso a cargar en CPU
     */
    private void dispatchDefault(ProcessControlBlock candidate) {
        if (candidate == null || cpu == null || !cpu.isIdle()) {
            return;
        }
        cpu.loadProcess(candidate);
    }

    /**
     * Verifica si el proceso actual ya completó todas sus instrucciones y lo marca como terminado.
     */
    private void finalizeProcessIfCompleted() {
        if (cpu == null) {
            return;
        }
        ProcessControlBlock runningProcess = cpu.getCurrentProcess();
        if (runningProcess == null) {
            return;
        }
        int totalInstructions = runningProcess.getTotalInstructions();
        if (runningProcess.getProgramCounter() > totalInstructions) {
            markAsFinished(runningProcess);
            cpu.releaseProcess();
            LOGGER.info(() -> String.format("Proceso %s (#%d) completó su ejecución en el ciclo #%d",
                    runningProcess.getProcessName(),
                    runningProcess.getProcessId(),
                    globalClockCycle.get()));
        }
    }

    /**
     * Actualiza un proceso a un estado objetivo, colocando el PCB en la cola correspondiente
     * y ajustando contadores de memoria según el cambio.
     * @param pcb proceso a actualizar
     * @param targetState estado objetivo
     * @param targetQueue cola donde se inserta el proceso
     * @param targetQueueName nombre lógico de la cola para logging
     */
    private void updateProcessState(ProcessControlBlock pcb,
                                    ProcessState targetState,
                                    CustomQueue<ProcessControlBlock> targetQueue,
                                    String targetQueueName) {
        Objects.requireNonNull(pcb, "El proceso no puede ser nulo");
        synchronized (stateLock) {
            ProcessState previousState = pcb.getProcessState();
            adjustMemoryCounters(previousState, targetState);
            pcb.setProcessState(targetState);
            if (targetState == ProcessState.LISTO) {
                pcb.markReadyQueueArrival(globalClockCycle.get());
            } else {
                pcb.clearReadyQueueArrival();
            }
            targetQueue.enqueue(pcb);
            logTransition(pcb, previousState, targetState, targetQueueName);
        }
    }

    /**
     * Registra la transición de estado con información contextual.
     * @param pcb proceso que cambió de estado
     * @param fromState estado previo
     * @param toState estado nuevo
     * @param queueName nombre de la cola destino
     */
    private void logTransition(ProcessControlBlock pcb,
                               ProcessState fromState,
                               ProcessState toState,
                               String queueName) {
        LOGGER.info(() -> String.format("Proceso %s (#%d) %s -> %s en %s",
                pcb.getProcessName(),
                pcb.getProcessId(),
                fromState,
                toState,
                queueName));
    }

    /**
     * Garantiza la capacidad de memoria suspendiendo candidatos cuando se intenta ingresar
     * un proceso adicional sin espacio disponible.
     */
    private void ensureCapacity() {
        while (processesInMemory >= maxProcessesInMemory) {
            ProcessControlBlock candidate = readyQueue.dequeue();
            if (candidate != null) {
                suspendCandidate(candidate, ProcessState.LISTO_SUSPENDIDO, readySuspendedQueue, "readySuspendedQueue");
                continue;
            }
            candidate = blockedQueue.dequeue();
            if (candidate != null) {
                suspendCandidate(candidate, ProcessState.BLOQUEADO_SUSPENDIDO, blockedSuspendedQueue, "blockedSuspendedQueue");
                continue;
            }
            throw new IllegalStateException("No hay procesos disponibles para suspender y la memoria está llena");
        }
    }

    /**
     * Suspende el proceso indicado, ajusta los contadores y registra la transición.
     * @param candidate proceso a suspender
     * @param suspendedState estado suspendido objetivo
     * @param targetQueue cola de suspendidos donde se almacenará
     * @param queueName nombre descriptivo de la cola suspendida
     */
    private void suspendCandidate(ProcessControlBlock candidate,
                                  ProcessState suspendedState,
                                  CustomQueue<ProcessControlBlock> targetQueue,
                                  String queueName) {
        ProcessState previousState = candidate.getProcessState();
        candidate.setProcessState(suspendedState);
        candidate.clearReadyQueueArrival();
        targetQueue.enqueue(candidate);
        processesInMemory = Math.max(0, processesInMemory - 1);
        logTransition(candidate, previousState, suspendedState, queueName);
    }

    /**
     * Restaura un proceso suspendido cuando existe capacidad disponible en memoria.
     */
    private void restoreSuspendedProcessIfPossible() {
        if (processesInMemory >= maxProcessesInMemory) {
            return;
        }
        ProcessControlBlock candidate = readySuspendedQueue.dequeue();
        if (candidate != null) {
            updateProcessState(candidate, ProcessState.LISTO, readyQueue, "readyQueue");
            return;
        }
        candidate = blockedSuspendedQueue.dequeue();
        if (candidate != null) {
            updateProcessState(candidate, ProcessState.BLOQUEADO, blockedQueue, "blockedQueue");
        }
    }

    /**
     * Ajusta el contador de procesos en memoria y asegura la capacidad según
     * la transición entre estados.
     * @param previousState estado anterior del proceso
     * @param targetState estado posterior del proceso
     */
    private void adjustMemoryCounters(ProcessState previousState, ProcessState targetState) {
        boolean wasInMemory = isInMemoryState(previousState);
        boolean willBeInMemory = isInMemoryState(targetState);
        if (!wasInMemory && willBeInMemory) {
            ensureCapacity();
            processesInMemory++;
        } else if (wasInMemory && !willBeInMemory) {
            processesInMemory = Math.max(0, processesInMemory - 1);
        }
    }

    /**
     * Determina si un estado implica que el proceso reside en memoria principal.
     * @param state estado a evaluar
     * @return true si el proceso se considera cargado en memoria principal
     */
    private boolean isInMemoryState(ProcessState state) {
        return state == ProcessState.LISTO
            || state == ProcessState.BLOQUEADO
            || state == ProcessState.EJECUCION;
    }

    public void setQueueListener(QueueListener listener) {
        synchronized (stateLock) {
            queueListener = listener;
        }
        notifyQueueListener();
    }

    private void notifyQueueListener() {
        QueueListener listenerSnapshot;
        List<ProcessControlBlock> readySnapshot;
        List<ProcessControlBlock> blockedSnapshot;
        List<ProcessControlBlock> finishedSnapshot;
        List<ProcessControlBlock> readySuspendedSnapshot;
        List<ProcessControlBlock> blockedSuspendedSnapshot;
        synchronized (stateLock) {
            listenerSnapshot = queueListener;
            if (listenerSnapshot == null) {
                return;
            }
            readySnapshot = snapshotQueue(readyQueue);
            blockedSnapshot = snapshotQueue(blockedQueue);
            finishedSnapshot = snapshotQueue(finishedProcessesList);
            readySuspendedSnapshot = snapshotQueue(readySuspendedQueue);
            blockedSuspendedSnapshot = snapshotQueue(blockedSuspendedQueue);
        }
        listenerSnapshot.onQueuesUpdated(readySnapshot, blockedSnapshot, finishedSnapshot, readySuspendedSnapshot, blockedSuspendedSnapshot);
    }

    private List<ProcessControlBlock> snapshotQueue(CustomQueue<ProcessControlBlock> queue) {
        Object[] raw = queue.getAllProcesses();
        List<ProcessControlBlock> items = new ArrayList<>(raw.length);
        for (Object value : raw) {
            if (value instanceof ProcessControlBlock pcb) {
                items.add(pcb);
            }
        }
        return Collections.unmodifiableList(items);
    }

    public void setCpuListener(CpuListener listener) {
        synchronized (stateLock) {
            cpuListener = listener;
        }
        notifyCpuListener();
    }

    private void notifyCpuListener() {
        CpuListener listenerSnapshot;
        ProcessControlBlock currentProcess;
        long currentCycle;
        CpuMode mode;
        synchronized (stateLock) {
            listenerSnapshot = cpuListener;
            if (listenerSnapshot == null) {
                return;
            }
            currentProcess = cpu != null ? cpu.getCurrentProcess() : null;
            currentCycle = globalClockCycle.get();
            mode = currentProcess == null ? CpuMode.OS : CpuMode.USUARIO;
        }
        listenerSnapshot.onCpuUpdated(currentProcess, currentCycle, mode);
    }

    @FunctionalInterface
    public interface QueueListener {
        void onQueuesUpdated(List<ProcessControlBlock> ready,
                             List<ProcessControlBlock> blocked,
                             List<ProcessControlBlock> finished,
                             List<ProcessControlBlock> readySuspended,
                             List<ProcessControlBlock> blockedSuspended);
    }

    @FunctionalInterface
    public interface CpuListener {
        void onCpuUpdated(ProcessControlBlock current,
                          long clockCycle,
                          CpuMode mode);
    }

    public enum CpuMode {
        OS("OS"),
        USUARIO("Usuario");

        private final String displayName;

        CpuMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
