/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;
import java.util.logging.Logger;

/**
 * SPN (Shortest Process Next) selecciona el proceso con menor cantidad de instrucciones pendientes.
 * Política no expropiativa que minimiza el tiempo de espera promedio en sistemas batch, aunque puede
 * provocar inanición de trabajos largos cuando llegan muchos procesos cortos consecutivos.
 */
public class SPN implements SchedulingPolicy {

    /** Logger para registrar la selección del proceso más corto. */
    private static final Logger LOGGER = Logger.getLogger(SPN.class.getName());

    /**
     * Encuentra el proceso con menor duración total y lo extrae de la cola de listos.
     * @param readyQueue cola de procesos listos controlada por el sistema operativo
     * @param currentProcess proceso actualmente en ejecución (no aplicable en SPN)
     * @return proceso más corto disponible o null si la cola está vacía
     */
    @Override
    public ProcessControlBlock selectNextProcess(CustomQueue<ProcessControlBlock> readyQueue,
                                                 ProcessControlBlock currentProcess,
                                                 long currentCycle) {
        if (readyQueue == null || readyQueue.isEmpty()) {
            return null;
        }

        Object[] snapshot = readyQueue.getAllProcesses();
        ProcessControlBlock shortest = null;
        for (int i = 0; i < snapshot.length; i++) {
            ProcessControlBlock pcb = (ProcessControlBlock) snapshot[i];
            if (pcb == null) {
                continue;
            }
            if (shortest == null || isShorter(pcb, shortest)) {
                shortest = pcb;
            }
        }

        if (shortest == null) {
            return readyQueue.dequeue();
        }

        readyQueue.remove(shortest);
        ProcessControlBlock selected = shortest;
        LOGGER.info(() -> String.format("SPN seleccionó %s (#%d) con %d instrucciones",
                selected.getProcessName(),
                selected.getProcessId(),
                selected.getTotalInstructions()));
        return selected;
    }

    /**
     * Determina si el candidato es más corto que el proceso actualmente seleccionado.
     * Usa el ID como desempate para garantizar estabilidad en procesos con igual duración.
     * @param candidate proceso candidato a comparación
     * @param current proceso actualmente considerado como más corto
     * @return true si candidate debe reemplazar a current
     */
    private boolean isShorter(ProcessControlBlock candidate, ProcessControlBlock current) {
        int diff = Integer.compare(candidate.getTotalInstructions(), current.getTotalInstructions());
        if (diff < 0) {
            return true;
        }
        if (diff > 0) {
            return false;
        }
        return candidate.getProcessId() < current.getProcessId();
    }
}
