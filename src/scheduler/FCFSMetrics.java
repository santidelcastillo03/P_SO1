/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to editar este template
 */
package scheduler;

/**
 * FCFSMetrics provee utilidades para evaluar métricas asociadas a la política FCFS.
 */
public final class FCFSMetrics {

    private FCFSMetrics() {
        // Evita instanciación
    }

    /**
     * Calcula el tiempo de espera promedio para las ráfagas proporcionadas.
     * @param burstDurations arreglos con la duración de CPU (en ciclos) de cada proceso
     * @return tiempo de espera promedio en ciclos
     */
    public static double calculateAverageWaitingTime(int[] burstDurations) {
        if (burstDurations == null || burstDurations.length == 0) {
            return 0.0;
        }
        long cumulative = 0L;
        long totalWaitingTime = 0L;
        for (int i = 0; i < burstDurations.length; i++) {
            if (i > 0) {
                totalWaitingTime += cumulative;
            }
            cumulative += Math.max(0, burstDurations[i]);
        }
        return totalWaitingTime / (double) burstDurations.length;
    }

    /**
     * Calcula el throughput como procesos completados por ciclo de CPU.
     * @param burstDurations duración de cada proceso en ciclos
     * @return throughput expresado en procesos por ciclo
     */
    public static double calculateThroughput(int[] burstDurations) {
        if (burstDurations == null || burstDurations.length == 0) {
            return 0.0;
        }
        long totalTime = 0L;
        for (int burst : burstDurations) {
            totalTime += Math.max(0, burst);
        }
        if (totalTime == 0L) {
            return 0.0;
        }
        return burstDurations.length / (double) totalTime;
    }
}
