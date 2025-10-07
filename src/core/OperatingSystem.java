/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package core;

import datastructures.CustomQueue;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OperatingSystem coordina las transiciones de estado de los procesos y administra
 * las colas de listos, bloqueados, finalizados y suspendidos, asegurando que la
 * capacidad de memoria principal se respete en todo momento.
 */
public class OperatingSystem {

    /** Logger para trazabilidad de cambios de estado. */
    private static final Logger LOGGER = Logger.getLogger(OperatingSystem.class.getName());
    /** Capacidad máxima de procesos residentes en memoria principal. */
    private static final int MAX_PROCESSES_IN_MEMORY = 4;

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
    }

    /**
     * Mueve el proceso a la cola de listos; si proviene de suspendido o nuevo se garantiza
     * antes que exista espacio en memoria principal.
     * @param pcb bloque de control que debe ir a ready
     */
    public void moveToReady(ProcessControlBlock pcb) {
        updateProcessState(pcb, ProcessState.LISTO, readyQueue, "readyQueue");
    }

    /**
     * Mueve el proceso a la cola de bloqueados manteniendo la coherencia de memoria.
     * @param pcb bloque de control que debe ir a bloqueados
     */
    public void moveToBlocked(ProcessControlBlock pcb) {
        updateProcessState(pcb, ProcessState.BLOQUEADO, blockedQueue, "blockedQueue");
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
        while (processesInMemory >= MAX_PROCESSES_IN_MEMORY) {
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
        targetQueue.enqueue(candidate);
        processesInMemory = Math.max(0, processesInMemory - 1);
        logTransition(candidate, previousState, suspendedState, queueName);
    }

    /**
     * Restaura un proceso suspendido cuando existe capacidad disponible en memoria.
     */
    private void restoreSuspendedProcessIfPossible() {
        if (processesInMemory >= MAX_PROCESSES_IN_MEMORY) {
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
}
