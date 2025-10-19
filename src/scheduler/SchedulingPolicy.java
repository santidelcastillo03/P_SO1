/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to editar este template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;

/**
 * SchedulingPolicy define la firma que deben implementar todas las políticas de planificación.
 * Permite seleccionar el próximo proceso a ejecutar considerando la cola de listos y el proceso actual.
 */
@FunctionalInterface
public interface SchedulingPolicy {

    /**
     * Selecciona el siguiente proceso que debe ejecutar la CPU usando la estrategia concreta.
     * @param readyQueue cola de procesos listos disponible para planificación
     * @param currentProcess proceso actualmente en CPU, o null si la CPU está ociosa
     * @return proceso a ejecutar en el siguiente ciclo o null si no hay candidatos
     */
    ProcessControlBlock selectNextProcess(CustomQueue<ProcessControlBlock> readyQueue,
                                          ProcessControlBlock currentProcess,
                                          long currentCycle);
}
