/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit este template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;
import java.util.logging.Logger;

/**
 * Feedback implementa colas multinivel con quantums crecientes para balancear equidad y throughput.
 */
public class Feedback implements SchedulingPolicy {

    private static final Logger LOGGER = Logger.getLogger(Feedback.class.getName());
    private static final int LEVELS = 4;

    private final CustomQueue<ProcessControlBlock>[] queues;

    public Feedback() {
        queues = new CustomQueue[LEVELS];
        for (int i = 0; i < LEVELS; i++) {
            queues[i] = new CustomQueue<>();
        }
    }

    @Override
    public ProcessControlBlock selectNextProcess(CustomQueue<ProcessControlBlock> readyQueue,
                                                 ProcessControlBlock currentProcess,
                                                 long currentCycle) {
        if (readyQueue != null && !readyQueue.isEmpty()) {
            drainExternalQueue(readyQueue);
        }
        for (int level = 0; level < LEVELS; level++) {
            if (!queues[level].isEmpty()) {
                ProcessControlBlock selected = queues[level].dequeue();
                selected.setPriorityLevel(level);
                final ProcessControlBlock chosen = selected;
                final int logLevel = level;
                LOGGER.fine(() -> String.format("Feedback selecciona %s (#%d) desde nivel %d",
                        chosen.getProcessName(),
                        chosen.getProcessId(),
                        logLevel));
                return selected;
            }
        }
        return null;
    }

    private void drainExternalQueue(CustomQueue<ProcessControlBlock> external) {
        ProcessControlBlock pcb;
        while ((pcb = external.dequeue()) != null) {
            int level = clampLevel(pcb.getPriorityLevel());
            pcb.setPriorityLevel(level);
            queues[level].enqueue(pcb);
        }
    }

    private int clampLevel(int level) {
        if (level < 0) {
            return 0;
        }
        if (level >= LEVELS) {
            return LEVELS - 1;
        }
        return level;
    }
}
