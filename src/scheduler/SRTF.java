/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;

/**
 * Placeholder para Shortest Remaining Time First.
 * A falta de métricas de ráfaga restante trabaja como FCFS.
 */
public class SRTF implements SchedulingPolicy {

    /**
     * Selecciona el siguiente proceso en cola mientras se incorporan tiempos restantes reales.
     * @param readyQueue cola de procesos listos
     * @param currentProcess proceso actual en CPU (sin uso hasta implementar la expropiación)
     * @return siguiente proceso o null si la cola está vacía
     */
    @Override
    public ProcessControlBlock selectNextProcess(CustomQueue<ProcessControlBlock> readyQueue,
                                                 ProcessControlBlock currentProcess) {
        if (readyQueue == null) {
            return null;
        }
        return readyQueue.dequeue();
    }
}
