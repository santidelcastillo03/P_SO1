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
 * OperatingSystem coordina las transiciones de estado de los procesos y las almacena en colas dedicadas
 * para simular el ciclo de vida ready/bloqueado/terminado de un sistema operativo real.
 * <p>
 * Proporciona métodos atómicos y sincronizados para mover procesos entre colas y cambiar su estado.
 * </p>
 */
public class OperatingSystem {

    /** Logger para mensajes de depuración y seguimiento de transiciones. */
    private static final Logger LOGGER = Logger.getLogger(OperatingSystem.class.getName());

    /** Cola de procesos listos para ejecutar. */
    private final CustomQueue<ProcessControlBlock> readyQueue;
    /** Cola de procesos bloqueados (esperando I/O u otros eventos). */
    private final CustomQueue<ProcessControlBlock> blockedQueue;
    /** Lista/cola de procesos que ya han finalizado su ejecución. */
    private final CustomQueue<ProcessControlBlock> finishedProcessesList;
    /** Objeto utilizado para sincronizar las transiciones de estado. */
    private final Object stateLock;

    /**
     * Construye un OperatingSystem con colas vacías para ready, blocked y finished.
     */
    public OperatingSystem() {
        this.readyQueue = new CustomQueue<>();
        this.blockedQueue = new CustomQueue<>();
        this.finishedProcessesList = new CustomQueue<>();
        this.stateLock = new Object();
    }

    /**
     * Mueve el proceso proporcionado a la cola de listos y lo marca como LISTO de forma atómica.
     * @param pcb bloque de control del proceso que debe pasar a la cola de listos
     */
    public void moveToReady(ProcessControlBlock pcb) {
        updateProcessState(pcb, ProcessState.LISTO, readyQueue, "readyQueue");
    }

    /**
     * Mueve el proceso proporcionado a la cola de bloqueados y lo marca como BLOQUEADO de forma atómica.
     * @param pcb bloque de control del proceso que debe pasar a la cola de bloqueados
     */
    public void moveToBlocked(ProcessControlBlock pcb) {
        updateProcessState(pcb, ProcessState.BLOQUEADO, blockedQueue, "blockedQueue");
    }

    /**
     * Marca un proceso como terminado, registra su tiempo de finalización si no está establecido
     * y lo encola en la lista de procesos finalizados.
     * @param pcb bloque de control del proceso que finalizó su ejecución
     */
    public void markAsFinished(ProcessControlBlock pcb) {
        Objects.requireNonNull(pcb, "El proceso no puede ser nulo");
        synchronized (stateLock) {
            ProcessState previousState = pcb.getProcessState();
            pcb.setProcessState(ProcessState.TERMINADO);
            if (pcb.getCompletionTime() < 0) {
                pcb.markCompleted(System.currentTimeMillis());
            }
            finishedProcessesList.enqueue(pcb);
            logTransition(pcb, previousState, ProcessState.TERMINADO, "finishedProcessesList");
        }
    }

    /**
     * Maneja la finalización de un evento de I/O: elimina el proceso de la cola de bloqueados
     * y lo reencola en la cola de listos.
     * @param pcb proceso que completó su operación de I/O
     */
    public void completeIo(ProcessControlBlock pcb) {
        Objects.requireNonNull(pcb, "El proceso no puede ser nulo");
        synchronized (stateLock) {
            boolean removed = blockedQueue.remove(pcb);
            if (!removed) {
                LOGGER.log(Level.WARNING, "No se encontró el proceso {0} en la cola de bloqueados", pcb.getProcessId());
            }
            ProcessState previousState = pcb.getProcessState();
            pcb.setProcessState(ProcessState.LISTO);
            readyQueue.enqueue(pcb);
            logTransition(pcb, previousState, ProcessState.LISTO, "readyQueue");
        }
    }

    /**
     * Ofrece visibilidad del siguiente proceso que espera en la cola de listos
     * (útil para pruebas o monitorización).
     * @return siguiente proceso marcado como LISTO o null si la cola está vacía
     */
    public ProcessControlBlock peekReady() {
        synchronized (stateLock) {
            return readyQueue.peek();
        }
    }

    /**
     * Ofrece visibilidad del siguiente proceso que espera en la cola de bloqueados
     * (útil para pruebas o monitorización).
     * @return siguiente proceso marcado como BLOQUEADO o null si la cola está vacía
     */
    public ProcessControlBlock peekBlocked() {
        synchronized (stateLock) {
            return blockedQueue.peek();
        }
    }

    /**
     * Ofrece visibilidad del siguiente proceso en la cola de finalizados
     * (útil para pruebas o monitorización).
     * @return siguiente proceso finalizado o null si la cola está vacía
     */
    public ProcessControlBlock peekFinished() {
        synchronized (stateLock) {
            return finishedProcessesList.peek();
        }
    }

    /**
     * Devuelve el número de procesos actualmente esperando en la cola de listos.
     * @return tamaño de la cola de listos
     */
    public int readyQueueSize() {
        synchronized (stateLock) {
            return readyQueue.size();
        }
    }

    /**
     * Devuelve el número de procesos actualmente esperando en la cola de bloqueados.
     * @return tamaño de la cola de bloqueados
     */
    public int blockedQueueSize() {
        synchronized (stateLock) {
            return blockedQueue.size();
        }
    }

    /**
     * Devuelve el número de procesos marcados como terminados.
     * @return tamaño de la lista de finalizados
     */
    public int finishedQueueSize() {
        synchronized (stateLock) {
            return finishedProcessesList.size();
        }
    }
    
    /**
     * Extrae y devuelve el siguiente proceso de la cola de listos (usado por el planificador).
     * Este método se utiliza cuando el planificador selecciona un proceso para despachar a la CPU.
     * @return siguiente proceso de la cola de listos o null si está vacía
     */
    public ProcessControlBlock dequeueReady() {
        synchronized (stateLock) {
            return readyQueue.dequeue();
        }
    }

    /**
     * Método interno que sincroniza el cambio de estado y la inserción en la cola
     * para las transiciones a LISTO o BLOQUEADO.
     * @param pcb proceso que se está actualizando
     * @param targetState estado que se debe aplicar al proceso
     * @param targetQueue cola donde se almacenará el proceso
     * @param targetQueueName etiqueta usada para el logging
     */
    private void updateProcessState(ProcessControlBlock pcb,
                                    ProcessState targetState,
                                    CustomQueue<ProcessControlBlock> targetQueue,
                                    String targetQueueName) {
        Objects.requireNonNull(pcb, "El proceso no puede ser nulo");
        synchronized (stateLock) {
            ProcessState previousState = pcb.getProcessState();
            pcb.setProcessState(targetState);
            targetQueue.enqueue(pcb);
            logTransition(pcb, previousState, targetState, targetQueueName);
        }
    }

    /**
     * Emite una entrada de log describiendo la transición de estado y la cola destino.
     * @param pcb proceso que realizó la transición
     * @param fromState estado anterior
     * @param toState nuevo estado
     * @param queueName cola que recibió el proceso
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
}
