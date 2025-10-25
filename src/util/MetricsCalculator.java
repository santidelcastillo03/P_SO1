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

    /**
     * Clase para almacenar métricas de rendimiento de una política
     */
    public static class PolicyMetrics {
        private String policyName;
        private int completedProcesses;
        private long totalCycles;
        private long startCycle;
        private long endCycle;

        public PolicyMetrics(String policyName) {
            this.policyName = policyName;
            this.completedProcesses = 0;
            this.totalCycles = 0;
            this.startCycle = -1;
            this.endCycle = -1;
        }

        public String getPolicyName() {
            return policyName;
        }

        public void setCompletedProcesses(int completedProcesses) {
            this.completedProcesses = completedProcesses;
        }

        public int getCompletedProcesses() {
            return completedProcesses;
        }

        public void incrementCompletedProcesses() {
            this.completedProcesses++;
        }

        public void setStartCycle(long startCycle) {
            this.startCycle = startCycle;
        }

        public void setEndCycle(long endCycle) {
            this.endCycle = endCycle;
            if (startCycle >= 0 && endCycle >= startCycle) {
                this.totalCycles = endCycle - startCycle;
            }
        }

        public long getTotalCycles() {
            return totalCycles;
        }

        /**
         * Calcula el throughput (procesos completados por ciclo)
         * @return throughput o 0 si no hay ciclos transcurridos
         */
        public double getThroughput() {
            if (totalCycles <= 0) {
                return 0.0;
            }
            return (double) completedProcesses / (double) totalCycles;
        }

        /**
         * Calcula el throughput con ciclos actuales si la simulación está corriendo
         * @param currentCycle ciclo actual del sistema
         * @return throughput calculado
         */
        public double getCurrentThroughput(long currentCycle) {
            if (startCycle < 0) {
                return 0.0;
            }
            long cycles = currentCycle - startCycle;
            if (cycles <= 0) {
                return 0.0;
            }
            return (double) completedProcesses / (double) cycles;
        }
    }
}
