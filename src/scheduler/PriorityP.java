/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;

/**
 * Placeholder para planificación por prioridad expropiativa.
 * Mientras no existan métricas de prioridad se comporta como FCFS.
 */
public class PriorityP implements SchedulingPolicy {

    /**
     * Selecciona el siguiente proceso siguiendo el orden de llegada por defecto.
     * @param readyQueue cola de listos
     * @param currentProcess proceso en ejecución (sin uso hasta implementar prioridades)
     * @return proceso elegido o null si no hay elementos
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
