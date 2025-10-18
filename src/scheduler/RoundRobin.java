/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;

/**
 * RoundRobin implementa una selección FIFO y delega la expropiación al contador de ciclos en CPU.
 * El quantum configurable se gestiona desde {@code CPU.setTimeQuantum(int)}.
 */
public class RoundRobin implements SchedulingPolicy {

    /** Quantum por defecto recomendado para RR (en ciclos). */
    public static final int DEFAULT_QUANTUM = 4;
    /** Valores admitidos de quantum para la política Round Robin. */
    public static final int[] SUPPORTED_QUANTA = {1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

    /**
     * Verifica si el quantum proporcionado pertenece al conjunto soportado.
     * @param quantum quantum en ciclos a validar
     * @return true si el valor está permitido
     */
    public static boolean isSupportedQuantum(int quantum) {
        for (int value : SUPPORTED_QUANTA) {
            if (value == quantum) {
                return true;
            }
        }
        return false;
    }

    /**
     * Devuelve el siguiente proceso listo siguiendo un orden FIFO.
     * @param readyQueue cola de listos administrada por el sistema operativo
     * @param currentProcess proceso en ejecución (no se usa en la selección básica)
     * @return siguiente proceso a ejecutar o null si la cola está vacía
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
