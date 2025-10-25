/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import core.ProcessControlBlock;

/**
 * Clase para calcular métricas de los procesos
 * @author Santiago
 */
public class MetricsCalculator {

    /**
     * Clase interna para almacenar el conteo de procesos
     */
    public static class ProcessTypeCount {
        private int ioBoundCount;
        private int cpuBoundCount;

        public ProcessTypeCount() {
            this.ioBoundCount = 0;
            this.cpuBoundCount = 0;
        }

        public void incrementIOBound() {
            this.ioBoundCount++;
        }

        public void incrementCPUBound() {
            this.cpuBoundCount++;
        }

        public int getIOBoundCount() {
            return ioBoundCount;
        }

        public int getCPUBoundCount() {
            return cpuBoundCount;
        }

        public int getTotalCount() {
            return ioBoundCount + cpuBoundCount;
        }
    }

    /**
     * Cuenta la cantidad de procesos I/O-bound y CPU-bound en un arreglo de PCBs
     * @param processes Arreglo de ProcessControlBlock a analizar
     * @return Objeto ProcessTypeCount con los conteos
     */
    public static ProcessTypeCount countProcessTypes(ProcessControlBlock[] processes) {
        ProcessTypeCount count = new ProcessTypeCount();

        if (processes == null) {
            return count;
        }

        for (int i = 0; i < processes.length; i++) {
            ProcessControlBlock pcb = processes[i];
            if (pcb != null) {
                if (pcb.isIOBound()) {
                    count.incrementIOBound();
                } else {
                    count.incrementCPUBound();
                }
            }
        }

        return count;
    }

    /**
     * Cuenta procesos de múltiples arreglos (colas)
     * @param processArrays Arreglos variables de ProcessControlBlock
     * @return Objeto ProcessTypeCount con los conteos totales
     */
    public static ProcessTypeCount countProcessTypesFromMultipleQueues(ProcessControlBlock[]... processArrays) {
        ProcessTypeCount totalCount = new ProcessTypeCount();

        if (processArrays == null) {
            return totalCount;
        }

        for (int i = 0; i < processArrays.length; i++) {
            ProcessControlBlock[] array = processArrays[i];
            ProcessTypeCount arrayCount = countProcessTypes(array);

            // Sumar los conteos
            for (int j = 0; j < arrayCount.getIOBoundCount(); j++) {
                totalCount.incrementIOBound();
            }
            for (int j = 0; j < arrayCount.getCPUBoundCount(); j++) {
                totalCount.incrementCPUBound();
            }
        }

        return totalCount;
    }

    /**
     * Cuenta un solo proceso si no es nulo
     * @param pcb ProcessControlBlock a contar
     * @param count Objeto ProcessTypeCount donde agregar el conteo
     */
    public static void countSingleProcess(ProcessControlBlock pcb, ProcessTypeCount count) {
        if (pcb != null && count != null) {
            if (pcb.isIOBound()) {
                count.incrementIOBound();
            } else {
                count.incrementCPUBound();
            }
        }
    }
}
