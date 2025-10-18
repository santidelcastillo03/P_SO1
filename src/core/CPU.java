/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package core;

import datastructures.CustomQueue;
import java.util.Objects;
import java.util.logging.Logger;
import scheduler.RoundRobin;
import scheduler.SRTF;
import scheduler.Scheduler;
import scheduler.SchedulingPolicy;
import util.IOHandler;

/**
 * CPU simula la ejecución de instrucciones para el proceso actualmente cargado y coordina sus transiciones de estado.
 * Se encarga de avanzar los registros de contexto, detectar eventos de I/O y delegar los cambios de estado al sistema operativo y al manejador de I/O.
 */
public class CPU {

    /** Logger para mensajes de depuración y seguimiento de eventos. */
    private static final Logger LOGGER = Logger.getLogger(CPU.class.getName());

    /** Referencia al sistema operativo que gestiona las colas de procesos. */
    private final OperatingSystem operatingSystem;
    /** Referencia al manejador de I/O que gestiona procesos bloqueados por entrada/salida. */
    private final IOHandler ioHandler;
    /** Proceso actualmente asignado a la CPU (puede ser null si está ociosa). */
    private ProcessControlBlock currentProcess;
    /** Planificador responsable de escoger el siguiente proceso listo. */
    private Scheduler scheduler;
    /** Ciclos ejecutados por el proceso actual desde su última carga (para RR). */
    private int cyclesExecutedByCurrentProcess;
    /** Quantum configurado para Round Robin (en ciclos). */
    private int timeQuantum;

    /**
     * Crea una CPU asociada al sistema operativo y al manejador de I/O.
     * @param operatingSystem orquestador que maneja las colas de estado de procesos
     * @param ioHandler componente responsable de la cola de espera de I/O
     */
    public CPU(OperatingSystem operatingSystem, IOHandler ioHandler) {
        this.operatingSystem = Objects.requireNonNull(operatingSystem, "El sistema operativo es obligatorio");
        this.ioHandler = Objects.requireNonNull(ioHandler, "El manejador de I/O es obligatorio");
        this.scheduler = null;
        this.cyclesExecutedByCurrentProcess = 0;
        this.timeQuantum = 4;
    }

    /**
     * Permite inyectar el planificador que utilizará la CPU para decidir qué proceso ejecutar.
     * @param scheduler instancia configurada del planificador
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = Objects.requireNonNull(scheduler, "El planificador no puede ser nulo");
    }

    /**
     * Devuelve el planificador actualmente asociado a la CPU.
     * @return instancia del planificador activo o null si no fue asignado
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * Permite cambiar la política activa del planificador en tiempo de ejecución.
     * @param policy política a utilizar desde este momento
     */
    public void setSchedulingPolicy(SchedulingPolicy policy) {
        if (scheduler == null) {
            throw new IllegalStateException("No es posible definir una política sin un Scheduler asociado");
        }
        scheduler.setPolicy(policy);
    }

    /**
     * Configura el quantum utilizado por la política Round Robin.
     * @param quantum cantidad de ciclos que puede ejecutar un proceso antes de ser expropiado
     */
    public void setTimeQuantum(int quantum) {
        if (!RoundRobin.isSupportedQuantum(quantum)) {
            throw new IllegalArgumentException("Quantum inválido para Round Robin: " + quantum);
        }
        this.timeQuantum = quantum;
    }

    /**
     * Devuelve el quantum actualmente configurado para Round Robin.
     * @return cantidad de ciclos permitidos por proceso
     */
    public int getTimeQuantum() {
        return timeQuantum;
    }

    /**
     * Carga el proceso proporcionado en la CPU y lo marca como en ejecución.
     * @param pcb proceso que la CPU debe ejecutar
     */
    public void loadProcess(ProcessControlBlock pcb) {
        Objects.requireNonNull(pcb, "El proceso a cargar no puede ser nulo");
        this.currentProcess = pcb;
        this.cyclesExecutedByCurrentProcess = 0;
        pcb.setProcessState(ProcessState.EJECUCION);
        LOGGER.info(() -> String.format("Proceso %s (#%d) cargado en CPU",
                pcb.getProcessName(),
                pcb.getProcessId()));
    }

    /**
     * Devuelve el proceso actualmente asignado a la CPU.
     * @return proceso activo o null si la CPU está ociosa
     */
    public ProcessControlBlock getCurrentProcess() {
        return currentProcess;
    }

    /**
     * Indica si la CPU está ociosa (sin proceso cargado).
     * @return true si no hay proceso cargado
     */
    public boolean isIdle() {
        return currentProcess == null;
    }

    /**
     * Solicita al planificador el siguiente proceso listo utilizando la cola proporcionada.
     * @param readyQueue cola de listos administrada por el sistema operativo
     * @return proceso elegido por la política o null si no hay candidatos
     */
    public ProcessControlBlock selectNextProcess(CustomQueue<ProcessControlBlock> readyQueue) {
        if (scheduler == null) {
            return readyQueue != null ? readyQueue.dequeue() : null;
        }
        return scheduler.selectNextProcess(readyQueue, currentProcess);
    }

    /**
     * Ejecuta un ciclo de CPU: avanza los registros de contexto y gestiona posibles bloqueos por I/O.
     */
    public void executeCycle() {
        if (currentProcess == null) {
            return;
        }

        // Avanza el contador de programa y el registro de dirección de memoria
        int nextProgramCounter = currentProcess.getProgramCounter() + 1;
        currentProcess.setProgramCounter(nextProgramCounter);
        currentProcess.setMemoryAddressRegister(currentProcess.getMemoryAddressRegister() + 1);
        cyclesExecutedByCurrentProcess++;

        // Verifica si debe ocurrir un evento de I/O
        triggerIoIfNeeded(nextProgramCounter);

        // Si el proceso fue expropiado o bloqueado, no evaluar quantum
        if (currentProcess == null) {
            cyclesExecutedByCurrentProcess = 0;
            return;
        }

        // Evalúa expropiación por quantum únicamente cuando Round Robin está activo
        handleQuantumExpiration();
        // Evalúa posible expropiación por SRTF si llega un proceso con menor tiempo restante
        handleShortestRemainingPreemption();
    }

    /**
     * Libera el proceso actualmente cargado en la CPU (la deja ociosa).
     * Útil cuando un proceso termina y debe ser removido de la CPU.
     */
    public void releaseProcess() {
        if (currentProcess != null) {
            LOGGER.info(() -> String.format("CPU libera proceso %s (#%d)",
                    currentProcess.getProcessName(),
                    currentProcess.getProcessId()));
            currentProcess = null;
            cyclesExecutedByCurrentProcess = 0;
        }
    }

    /**
     * Verifica si el proceso en ejecución debe pasar a BLOQUEADO por evento de I/O y coordina la transición.
     * @param programCounter valor alcanzado tras ejecutar el ciclo
     */
    private void triggerIoIfNeeded(int programCounter) {
        if (currentProcess == null) {
            return;
        }
        int ioCycle = currentProcess.getIoExceptionCycle();
        if (ioCycle >= 0 && programCounter == ioCycle) {
            ProcessControlBlock processToBlock = currentProcess;
            operatingSystem.moveToBlocked(processToBlock);
            ioHandler.enqueueProcess(processToBlock);
            currentProcess = null;
            cyclesExecutedByCurrentProcess = 0;
            LOGGER.info(() -> String.format("Proceso %s (#%d) movido a BLOQUEADO por evento de I/O",
                    processToBlock.getProcessName(),
                    processToBlock.getProcessId()));
        }
    }

    /**
     * Gestiona la posible expropiación del proceso en ejecución cuando se agota el quantum.
     */
    private void handleQuantumExpiration() {
        if (!isRoundRobinActive()) {
            return;
        }
        int totalInstructions = currentProcess.getTotalInstructions();
        if (totalInstructions > 0 && currentProcess.getProgramCounter() >= totalInstructions) {
            return;
        }
        if (cyclesExecutedByCurrentProcess < timeQuantum) {
            return;
        }
        ProcessControlBlock processToRequeue = currentProcess;
        operatingSystem.moveToReady(processToRequeue);
        currentProcess = null;
        LOGGER.info(() -> String.format("Proceso %s (#%d) expropiado tras alcanzar quantum RR=%d",
                processToRequeue.getProcessName(),
                processToRequeue.getProcessId(),
                timeQuantum));
        cyclesExecutedByCurrentProcess = 0;
    }

    /**
     * Determina si la política activa corresponde a Round Robin.
     * @return true cuando RR está configurado en el scheduler
     */
    private boolean isRoundRobinActive() {
        return scheduler != null && scheduler.getActivePolicy() instanceof RoundRobin;
    }

    /**
     * Gestiona la expropiación cuando hay un proceso con menor tiempo restante (SRTF).
     */
    private void handleShortestRemainingPreemption() {
        if (!isSrtActive() || currentProcess == null) {
            return;
        }

        ProcessControlBlock[] snapshot = operatingSystem.getReadyQueueSnapshot();
        if (snapshot == null || snapshot.length == 0) {
            return;
        }

        ProcessControlBlock shortest = null;
        for (int i = 0; i < snapshot.length; i++) {
            ProcessControlBlock candidate = snapshot[i];
            if (candidate == null) {
                continue;
            }
            if (shortest == null || compareRemainingTime(candidate, shortest) < 0) {
                shortest = candidate;
            }
        }

        if (shortest == null) {
            return;
        }

        int currentRemaining = remainingTime(currentProcess);
        int candidateRemaining = remainingTime(shortest);
        if (candidateRemaining >= currentRemaining) {
            return;
        }

        if (!operatingSystem.removeFromReadyQueue(shortest)) {
            return;
        }

        ProcessControlBlock preempted = currentProcess;
        ProcessControlBlock selectedProcess = shortest;
        final int finalCandidateRemaining = candidateRemaining;
        currentProcess = null;
        cyclesExecutedByCurrentProcess = 0;
        LOGGER.info(() -> String.format("Proceso %s (#%d) expropiado por SRTF; nuevo proceso %s (#%d) restante=%d",
                preempted.getProcessName(),
                preempted.getProcessId(),
                selectedProcess.getProcessName(),
                selectedProcess.getProcessId(),
                finalCandidateRemaining));

        operatingSystem.moveToReady(preempted);
        loadProcess(selectedProcess);
    }

    /**
     * Determina si la política activa corresponde a Shortest Remaining Time First.
     * @return true cuando SRTF está habilitado
     */
    private boolean isSrtActive() {
        return scheduler != null && scheduler.getActivePolicy() instanceof SRTF;
    }

    /**
     * Compara el tiempo restante de dos procesos.
     * @param candidate proceso candidato
     * @param current proceso actualmente considerado más corto
     * @return valor negativo si candidate tiene menor restante
     */
    private int compareRemainingTime(ProcessControlBlock candidate, ProcessControlBlock current) {
        int diff = remainingTime(candidate) - remainingTime(current);
        if (diff != 0) {
            return diff;
        }
        return candidate.getProcessId() - current.getProcessId();
    }

    /**
     * Calcula el tiempo restante de ejecución para el proceso indicado.
     * @param pcb proceso a evaluar
     * @return cantidad de instrucciones pendientes por ejecutar
     */
    private int remainingTime(ProcessControlBlock pcb) {
        if (pcb == null) {
            return Integer.MAX_VALUE;
        }
        int remaining = pcb.getTotalInstructions() - pcb.getProgramCounter();
        return remaining < 0 ? 0 : remaining;
    }
}
