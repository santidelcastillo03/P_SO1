/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;
import java.util.logging.Logger;

/**
 * SRTF (Shortest Remaining Time First) selecciona el proceso con menor tiempo restante.
 * Se utiliza cuando la CPU está libre; la lógica de expropiación se maneja en la CPU después
 * de cada ciclo evaluando si llegó un proceso más corto.
 */
public class SRTF implements SchedulingPolicy {

    /** Logger para diagnosticar las decisiones de SRTF. */
    private static final Logger LOGGER = Logger.getLogger(SRTF.class.getName());

    /**
     * Selecciona el proceso con menor tiempo restante cuando la CPU está ociosa.
     * @param readyQueue cola de listos disponible
     * @param currentProcess proceso actualmente en CPU (si existe, no se expropia aquí)
     * @return proceso a ejecutar o null si no hay candidatos
     */
    @Override
    public ProcessControlBlock selectNextProcess(CustomQueue<ProcessControlBlock> readyQueue,
                                                 ProcessControlBlock currentProcess) {
        if (readyQueue == null || readyQueue.isEmpty()) {
            return null;
        }
        if (currentProcess != null) {
            return null;
        }

        Object[] snapshot = readyQueue.getAllProcesses();
        ProcessControlBlock shortest = null;
        for (int i = 0; i < snapshot.length; i++) {
            ProcessControlBlock candidate = (ProcessControlBlock) snapshot[i];
            if (candidate == null) {
                continue;
            }
            if (shortest == null || compareRemaining(candidate, shortest) < 0) {
                shortest = candidate;
            }
        }

        if (shortest == null) {
            return readyQueue.dequeue();
        }

        readyQueue.remove(shortest);
        ProcessControlBlock selected = shortest;
        LOGGER.info(() -> String.format("SRTF carga %s (#%d) con restante=%d",
                selected.getProcessName(),
                selected.getProcessId(),
                remainingTime(selected)));
        return selected;
    }

    private int compareRemaining(ProcessControlBlock candidate, ProcessControlBlock current) {
        int diff = remainingTime(candidate) - remainingTime(current);
        if (diff != 0) {
            return diff;
        }
        return candidate.getProcessId() - current.getProcessId();
    }

    private int remainingTime(ProcessControlBlock pcb) {
        int remaining = pcb.getTotalInstructions() - pcb.getProgramCounter();
        return remaining < 0 ? 0 : remaining;
    }
}
