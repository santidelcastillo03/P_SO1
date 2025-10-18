/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to editar este template
 */
package scheduler;

/**
 * PolicyType enumera las políticas de planificación soportadas por el simulador.
 */
public enum PolicyType {
    /** Política First Come, First Served (no expropiativa). */
    FCFS,
    /** Política Round Robin con reparto equitativo de quantum. */
    ROUND_ROBIN,
    /** Shortest Process Next (SPN) no expropiativa. */
    SPN,
    /** Shortest Remaining Time (SRT) expropiativa. */
    SRT,
    /** Highest Response Ratio Next (HRRN) no expropiativa. */
    HRRN,
    /** Multi-Level Feedback Queue (retroalimentación por niveles). */
    FEEDBACK
}
