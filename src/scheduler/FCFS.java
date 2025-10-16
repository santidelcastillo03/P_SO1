/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;
import java.util.logging.Logger;

/**
 * FCFS (First Come, First Served) selecciona procesos en orden de llegada sin expropiación.
 * Presenta efecto convoy: un proceso largo puede retrasar significativamente a procesos cortos.
 */
public class FCFS implements SchedulingPolicy {

    /** Logger para trazar el proceso seleccionado en cada ciclo. */
    private static final Logger LOGGER = Logger.getLogger(FCFS.class.getName());

    /**
     * Extrae el proceso al frente de la cola de listos (FIFO) y registra la selección.
     * @param readyQueue cola de listos del sistema operativo
     * @param currentProcess proceso actualmente en CPU (no utilizado en FCFS)
     * @return siguiente proceso en cola o null si está vacía
     */
    @Override
    public ProcessControlBlock selectNextProcess(CustomQueue<ProcessControlBlock> readyQueue,
                                                 ProcessControlBlock currentProcess) {
        if (readyQueue == null) {
            return null;
        }
        ProcessControlBlock next = readyQueue.dequeue();
        if (next != null) {
            LOGGER.info(() -> String.format("Seleccionado: %s", next.getProcessName()));
        }
        return next;
    }
}
