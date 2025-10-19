/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;
import java.util.logging.Logger;

/**
 * HRRN (Highest Response Ratio Next) prioriza procesos considerando tiempo de espera y duración.
 * Evita la inanición de procesos largos al aumentar su prioridad a medida que esperan en ready.
 */
public class HRRN implements SchedulingPolicy {

    /** Logger para registrar las decisiones de la política HRRN. */
    private static final Logger LOGGER = Logger.getLogger(HRRN.class.getName());

    /**
     * Selecciona el proceso con mayor ratio de respuesta.
     * @param readyQueue cola de procesos listos
     * @param currentProcess proceso actualmente en ejecución (no se usa en HRRN clásico)
     * @param currentCycle ciclo global del sistema para calcular tiempos de espera
     * @return proceso a ejecutar o null si no hay candidatos
     */
    @Override
    public ProcessControlBlock selectNextProcess(CustomQueue<ProcessControlBlock> readyQueue,
                                                 ProcessControlBlock currentProcess,
                                                 long currentCycle) {
        if (readyQueue == null || readyQueue.isEmpty()) {
            return null;
        }

        Object[] snapshot = readyQueue.getAllProcesses();
        ProcessControlBlock best = null;
        double bestRatio = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < snapshot.length; i++) {
            ProcessControlBlock pcb = (ProcessControlBlock) snapshot[i];
            if (pcb == null) {
                continue;
            }
            double ratio = pcb.getResponseRatio(currentCycle);
            if (ratio > bestRatio || (ratio == bestRatio && isLowerId(pcb, best))) {
                bestRatio = ratio;
                best = pcb;
            }
        }

        if (best == null) {
            return readyQueue.dequeue();
        }

        readyQueue.remove(best);
        ProcessControlBlock selected = best;
        LOGGER.info(() -> String.format("HRRN seleccionó %s (#%d) con ratio=%.3f (espera=%d, total=%d)",
                selected.getProcessName(),
                selected.getProcessId(),
                selected.getResponseRatio(currentCycle),
                selected.getWaitingTime(currentCycle),
                selected.getTotalInstructions()));
        return selected;
    }

    private boolean isLowerId(ProcessControlBlock candidate, ProcessControlBlock current) {
        if (current == null) {
            return true;
        }
        return candidate.getProcessId() < current.getProcessId();
    }
}
