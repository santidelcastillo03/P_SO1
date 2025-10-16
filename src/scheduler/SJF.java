/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;

/**
 * Placeholder para Shortest Job First; falta integrar estimaciones de ráfaga.
 */
public class SJF implements SchedulingPolicy {

    /**
     * Hasta disponer de métricas de ráfaga, mantiene un comportamiento FIFO.
     * @param readyQueue cola de procesos listos
     * @param currentProcess proceso actual en CPU
     * @return proceso seleccionado o null si no hay disponibles
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
