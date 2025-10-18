/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;

/**
 * Placeholder para planificación por prioridad no expropiativa.
 * Actualmente delega en un orden FCFS hasta que se definan atributos de prioridad.
 */
public class PriorityNP implements SchedulingPolicy {

    /**
     * Extrae el primer proceso disponible mientras se implementan las prioridades reales.
     * @param readyQueue cola de listos administrada por el sistema operativo
     * @param currentProcess proceso actualmente en ejecución
     * @return proceso seleccionado o null si la cola está vacía
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
