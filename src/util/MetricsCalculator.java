/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import core.ProcessControlBlock;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * MetricsCalculator acumula la informacion necesaria para derivar indicadores de rendimiento
 * del simulador como throughput y tiempo de respuesta promedio.
 */
public class MetricsCalculator {

    /** Cantidad total de ciclos registrados desde el reinicio del simulador. */
    private long totalCycles;
    /** Número de procesos completados desde el reinicio del simulador. */
    private long completedProcesses;
    /** Suma acumulada de tiempos de respuesta para procesos completados. */
    private double sumResponseTime;
    /** Tabla con el ciclo de creación de cada proceso activo. */
    private final Map<Integer, Long> creationCycles;
    /** Tabla con el tiempo de respuesta registrado por cada proceso. */
    private final Map<Integer, Long> responseTimes;

    /**
     * Construye el calculador con contadores en cero y tablas vacías.
     */
    public MetricsCalculator() {
        this.creationCycles = new HashMap<>();
        this.responseTimes = new HashMap<>();
        reset();
    }

    /**
     * Registra un ciclo del reloj global para efectos estadisticos.
     * @param cpuBusy indicador del estado de la CPU (no se utiliza actualmente)
     */
    public synchronized void registerCycle(boolean cpuBusy) {
        totalCycles++;
    }

    /**
     * Registra la creación de un proceso almacenando su ciclo de origen para métricas posteriores.
     * @param pcb proceso creado
     * @param creationCycle ciclo global asociado a la creación
     */
    public synchronized void registerProcessCreation(ProcessControlBlock pcb, long creationCycle) {
        Objects.requireNonNull(pcb, "El proceso no puede ser nulo");
        creationCycles.put(pcb.getProcessId(), Math.max(0L, creationCycle));
    }

    /**
     * Registra la primera ejecución de un proceso para calcular su tiempo de respuesta.
     * @param pcb proceso que inicia su ejecución
     * @param firstExecutionCycle ciclo global cuando se atendió por primera vez
     */
    public synchronized void registerProcessFirstExecution(ProcessControlBlock pcb, long firstExecutionCycle) {
        Objects.requireNonNull(pcb, "El proceso no puede ser nulo");
        int processId = pcb.getProcessId();
        if (responseTimes.containsKey(processId)) {
            return;
        }
        long creationCycle = creationCycles.getOrDefault(processId, Math.max(0L, firstExecutionCycle));
        long response = Math.max(0L, firstExecutionCycle - creationCycle);
        responseTimes.put(processId, response);
        sumResponseTime += response;
    }

    /**
     * Registra la finalización de un proceso actualizando contadores y sumatorias asociadas.
     * @param pcb proceso que finalizó
     */
    public synchronized void registerProcessCompletion(ProcessControlBlock pcb) {
        Objects.requireNonNull(pcb, "El proceso no puede ser nulo");
        completedProcesses++;
        int processId = pcb.getProcessId();
        creationCycles.remove(processId);
        responseTimes.remove(processId);
    }

    /**
     * Calcula el throughput como procesos completados por ciclo ejecutado.
     * @return throughput expresado en procesos por ciclo
     */
    public synchronized double getThroughput() {
        if (totalCycles <= 0L) {
            return 0.0;
        }
        return completedProcesses / (double) totalCycles;
    }

    /**
     * Obtiene el tiempo de respuesta promedio considerando los procesos finalizados.
     * @return tiempo de respuesta medio en ciclos
     */
    public synchronized double getAvgResponseTime() {
        if (completedProcesses <= 0L) {
            return 0.0;
        }
        return sumResponseTime / completedProcesses;
    }

    /**
     * Restablece todos los contadores y tablas para comenzar una nueva medición.
     */
    public synchronized void reset() {
        totalCycles = 0L;
        completedProcesses = 0L;
        sumResponseTime = 0.0;
        creationCycles.clear();
        responseTimes.clear();
    }
}
