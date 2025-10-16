/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;

/**
 * Placeholder para Round Robin; en el futuro integrará control de quantum y cola circular.
 */
public class RoundRobin implements SchedulingPolicy {

    /**
     * Mientras se define el manejo de quantum, extrae el siguiente proceso disponible.
     * @param readyQueue cola de listos
     * @param currentProcess proceso en ejecución (sin uso temporalmente)
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
