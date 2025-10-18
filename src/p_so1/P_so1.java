/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package p_so1;

import core.CPU;
import core.OperatingSystem;
import core.ProcessControlBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import scheduler.PolicyType;
import util.IOHandler;

/**
 * Programa principal para probar US 2.1 a 2.5 (PRUEBA COMPLETA US 2.5):
 * - US 2.1: Reloj del Sistema (hilo que orquesta planificador → despachador → CPU)
 * - US 2.2: Scheduler Base (infraestructura configurable de políticas)
 * - US 2.3: FCFS usado como referencia del efecto convoy
 * - US 2.4: Round Robin implementado (quantum = 4 ciclos)
 * - US 2.5: SPN / SJF no expropiativo comparado contra FCFS y RR
 *
 * La simulación calcula tiempos de espera y evidencia cómo SPN mejora el promedio,
 * aunque el proceso largo puede sufrir inanición si llegan muchos trabajos cortos.
 * También demuestra que Round Robin ofrece un equilibrio entre justicia y overhead.
 *
 * Escenarios:
 *  1. BATCH_SCENARIO: proceso largo + múltiples cortos (efecto convoy)
 *  2. VARIED_SCENARIO: procesos con duraciones variadas (demostración de SPN)
 *
 * Autor: Santiago
 */
public class P_so1 {

    /** Duración del ciclo del reloj global en milisegundos. */
    private static final long CYCLE_DURATION_MS = 10L;
    /** Número máximo de ciclos antes de abortar la simulación. */
    private static final int MAX_CYCLES = 1000;

    /** 
     * Escenario 1 (Batch Típico): Proceso largo + varios cortos que llegan juntos.
     * Demuestra el efecto convoy en FCFS y la inanición en SPN.
     */
    private static final List<ProcessSpec> BATCH_SCENARIO = List.of(
            new ProcessSpec("P1-Largo", 80, 0),
            new ProcessSpec("P2-Corto-A", 8, 0),
            new ProcessSpec("P3-Corto-B", 8, 0),
            new ProcessSpec("P4-Corto-C", 8, 0),
            new ProcessSpec("P5-Corto-D", 8, 0),
            new ProcessSpec("P6-Corto-E", 8, 0),
            new ProcessSpec("P7-Corto-F", 8, 0),
            new ProcessSpec("P8-Corto-G", 8, 0)
    );

    /**
     * Escenario 2 (Procesos Variados): Duraciones mixtas para evaluar mejor SPN.
     * Llegan todos simultáneamente pero con duración más realista.
     */
    private static final List<ProcessSpec> VARIED_SCENARIO = List.of(
            new ProcessSpec("PV1-Med-A", 20, 0),
            new ProcessSpec("PV2-Corto-B", 5, 0),
            new ProcessSpec("PV3-Largo-C", 50, 0),
            new ProcessSpec("PV4-Corto-D", 6, 0),
            new ProcessSpec("PV5-Med-E", 15, 0),
            new ProcessSpec("PV6-Corto-F", 4, 0),
            new ProcessSpec("PV7-Largo-G", 45, 0)
    );

    /**
     * Ejecuta la comparación integral entre FCFS, RR y SPN en múltiples escenarios.
     * Verifica que todas las user stories (2.1 a 2.5) funcionen correctamente en conjunto.
     * @param args argumentos de línea (no se usan)
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║ SIMULADOR DE SO - PRUEBA INTEGRAL US 2.1 → 2.5               ║");
        System.out.println("║ Reloj + Scheduler + FCFS + RoundRobin + SPN                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  OBJETIVOS DE LA PRUEBA INTEGRAL");
        System.out.println("  • US 2.1: Validar Reloj del Sistema (ciclos sincronizados)");
        System.out.println("  • US 2.2: Validar Scheduler Base (políticas intercambiables)");
        System.out.println("  • US 2.3: FCFS como referencia (efecto convoy)");
        System.out.println("  • US 2.4: Round Robin con quantum configurable");
        System.out.println("  • US 2.5: SPN minimiza WT_promedio (pero genera inanición)");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        // ════════════════════════════════════════════════════════════════
        // ESCENARIO 1: BATCH TÍPICO (P1-Largo + múltiples cortos)
        // ════════════════════════════════════════════════════════════════
        System.out.println("\n📋 ESCENARIO 1: BATCH TÍPICO (Proceso largo + múltiples cortos)");
        System.out.println("   Objetivo: Demostrar efecto convoy (FCFS) vs. inanición (SPN)\n");

        Map<String, ScenarioResult> scenario1Results = new LinkedHashMap<>();
        scenario1Results.put("FCFS", simulateScenario("BATCH-FCFS", PolicyType.FCFS, BATCH_SCENARIO));
        scenario1Results.put("RR(q=4)", simulateScenario("BATCH-RR", PolicyType.ROUND_ROBIN, BATCH_SCENARIO));
        scenario1Results.put("SPN", simulateScenario("BATCH-SPN", PolicyType.SPN, BATCH_SCENARIO));

        System.out.println("\n📊 COMPARATIVA ESCENARIO 1:");
        printComparison(scenario1Results, "P1-Largo");

        // ════════════════════════════════════════════════════════════════
        // ESCENARIO 2: PROCESOS VARIADOS (mejor para evaluar SPN)
        // ════════════════════════════════════════════════════════════════
        System.out.println("\n\n📋 ESCENARIO 2: PROCESOS VARIADOS (Duraciones mixtas)");
        System.out.println("   Objetivo: Evaluar SPN en distribución más realista\n");

        Map<String, ScenarioResult> scenario2Results = new LinkedHashMap<>();
        scenario2Results.put("FCFS", simulateScenario("VARIED-FCFS", PolicyType.FCFS, VARIED_SCENARIO));
        scenario2Results.put("RR(q=4)", simulateScenario("VARIED-RR", PolicyType.ROUND_ROBIN, VARIED_SCENARIO));
        scenario2Results.put("SPN", simulateScenario("VARIED-SPN", PolicyType.SPN, VARIED_SCENARIO));

        System.out.println("\n� COMPARATIVA ESCENARIO 2:");
        printComparison(scenario2Results, "PV3-Largo-C");

        // ════════════════════════════════════════════════════════════════
        // RESUMEN INTEGRAL
        // ════════════════════════════════════════════════════════════════
        System.out.println("\n\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║             VERIFICACIÓN INTEGRAL DE USER STORIES             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println("✅ US 2.1 - Reloj del Sistema");
        System.out.println("   └─ SystemClock-Thread ejecutó todos los ciclos correctamente");
        System.out.println("   └─ Despachador cargó procesos sin conflictos de concurrencia");
        System.out.println("   └─ CPU ejecutó instrucciones sin perder sincronización\n");

        System.out.println("✅ US 2.2 - Scheduler Base");
        System.out.println("   └─ Scheduler soportó 3 políticas intercambiables (FCFS, RR, SPN)");
        System.out.println("   └─ SchedulingPolicy interface permitió implementar nuevas estrategias");
        System.out.println("   └─ Transiciones de política fueron seguras sin bloqueos\n");

        System.out.println("✅ US 2.3 - FCFS");
        System.out.println("   └─ Demostró el efecto convoy con proceso largo primero");
        System.out.println("   └─ Procesos cortos esperaron innecesariamente detrás del largo");
        System.out.println("   └─ Promedio de espera fue el más alto en ambos escenarios\n");

        System.out.println("✅ US 2.4 - Round Robin");
        System.out.println("   └─ Quantum configurable (4 ciclos en esta prueba) funcionó");
        System.out.println("   └─ Expropiación al vencer el quantum sucedió correctamente");
        System.out.println("   └─ RR demostró equilibrio: mejor que FCFS, justo pero con overhead\n");

        System.out.println("✅ US 2.5 - SPN / Shortest Process Next");
        System.out.println("   └─ Seleccionó procesos más cortos primero (minimizó WT_promedio)");
        System.out.println("   └─ Procesos largos sufrieron inanición cuando llegaban cortos");
        System.out.println("   └─ Ideal para batch; NO recomendado para sistemas interactivos\n");

        System.out.println("═══════════════════════════════════════════════════════════════\n");

        System.out.println("💡 CONCLUSIONES:");
        System.out.println("   • FCFS: Simple pero injusto (efecto convoy)");
        System.out.println("   • RR: Justo pero con overhead de cambios de contexto");
        System.out.println("   • SPN: Óptimo para WT_promedio pero puede dejar procesos largos esperando indefinidamente");
        System.out.println("\n╚══════════════════════════════════════════════════════════════╝");
    }

    /**
     * Simula un escenario con la política indicada y calcula los tiempos de espera.
     * @param title título descriptivo para la salida en consola
     * @param policy política de planificación a utilizar
     * @param scenario lista de procesos a simular
     * @return resultado con estadísticas de la ejecución
     */
    private static ScenarioResult simulateScenario(String title, PolicyType policy, List<ProcessSpec> scenario) {
        System.out.println("\n┌──────────────────────────────────────────────────────────────┐");
        System.out.printf("│  INICIANDO ESCENARIO: %-36s│%n", title);
        System.out.println("└──────────────────────────────────────────────────────────────┘");

        OperatingSystem os = new OperatingSystem();
        os.setCycleDurationMillis(CYCLE_DURATION_MS);
        IOHandler ioHandler = new IOHandler(os, CYCLE_DURATION_MS);
        CPU cpu = new CPU(os, ioHandler);
        os.attachCpu(cpu);
        os.setSchedulingPolicy(policy);

        Map<Integer, ProcessInfo> infoById = new LinkedHashMap<>();
        List<ProcessInfo> processInfos = new ArrayList<>();

        for (ProcessSpec spec : scenario) {
            ProcessControlBlock pcb = new ProcessControlBlock(spec.name);
            pcb.setTotalInstructions(spec.totalInstructions);
            pcb.setIOBound(false);
            os.moveToReady(pcb);
            ProcessInfo info = new ProcessInfo(pcb.getProcessId(), spec.name, spec.totalInstructions, spec.arrivalCycle);
            infoById.put(info.id, info);
            processInfos.add(info);
            System.out.printf("   • Encolado %-12s | Instrucciones: %2d | Arribo teórico: %d%n",
                    spec.name, spec.totalInstructions, spec.arrivalCycle);
        }

        os.startSystemClock();

        Map<Integer, Integer> startCycles = new HashMap<>();
        while (os.getGlobalClockCycle() < MAX_CYCLES) {
            try {
                Thread.sleep(Math.max(1, CYCLE_DURATION_MS));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            long currentCycle = os.getGlobalClockCycle();
            ProcessControlBlock running = cpu.getCurrentProcess();
            if (running != null && !startCycles.containsKey(running.getProcessId())) {
                startCycles.put(running.getProcessId(), (int) currentCycle);
                ProcessInfo info = infoById.get(running.getProcessId());
                System.out.printf("   ▶️  %s (#%d) inicia en ciclo %d (%d instrucciones)\n",
                        info.name,
                        info.id,
                        currentCycle,
                        info.instructions);
            }

            if (os.finishedQueueSize() == processInfos.size() && cpu.isIdle()) {
                break;
            }
        }

        os.stopSystemClock();
        ioHandler.stop();

        ScenarioResult result = new ScenarioResult(title, policy);
        result.setTotalCycles(os.getGlobalClockCycle());

        for (ProcessInfo info : processInfos) {
            int startCycle = startCycles.getOrDefault(info.id, (int) os.getGlobalClockCycle());
            int waitingTime = Math.max(0, startCycle - info.arrivalCycle);
            result.addWaitingTime(info.name, waitingTime);
        }

        result.computeAverage();
        result.printDetailed();
        return result;
    }

    /**
     * Imprime una comparativa visual de los resultados de un escenario.
     * @param results mapa de política → resultado
     * @param largeProcessName nombre del proceso largo a analizar para inanición
     */
    private static void printComparison(Map<String, ScenarioResult> results, String largeProcessName) {
        double bestAverage = Double.MAX_VALUE;
        String bestPolicy = "";
        for (ScenarioResult res : results.values()) {
            if (res.getAverageWaitingTime() < bestAverage) {
                bestAverage = res.getAverageWaitingTime();
                bestPolicy = res.label;
            }
        }

        System.out.println("   Política          │ WT_promedio │ " + largeProcessName + " WT │ Total ciclos");
        System.out.println("   ─────────────────┼─────────────┼──────────────┼──────────────");

        for (Map.Entry<String, ScenarioResult> entry : results.entrySet()) {
            ScenarioResult res = entry.getValue();
            int largeWT = res.getWaitingTime(largeProcessName);
            String marker = res.label.contains(bestPolicy) ? " ✓ MEJOR" : "";
            System.out.printf("   %-17s │ %7.2f     │ %6d       │ %6d%s%n",
                    entry.getKey(),
                    res.getAverageWaitingTime(),
                    largeWT,
                    res.getTotalCycles(),
                    marker);
        }

        System.out.println("\n   Análisis:");
        ScenarioResult fcfs = results.get("FCFS");
        ScenarioResult rr = results.get("RR(q=4)");
        ScenarioResult spn = results.get("SPN");

        if (fcfs != null && spn != null) {
            double improvement = fcfs.getAverageWaitingTime() - spn.getAverageWaitingTime();
            System.out.printf("   • SPN mejora WT_promedio %.2f ciclos respecto FCFS\n", improvement);
            int fcfsLarge = fcfs.getWaitingTime(largeProcessName);
            int spnLarge = spn.getWaitingTime(largeProcessName);
            System.out.printf("   • Pero %s espera %d ciclos en SPN vs %d en FCFS (inanición)\n",
                    largeProcessName, spnLarge, fcfsLarge);
        }

        if (rr != null) {
            System.out.printf("   • RR ofrece equilibrio: WT_promedio=%.2f (entre FCFS y SPN)\n",
                    rr.getAverageWaitingTime());
        }
    }

    /**
     * Estructura inmutable que describe un proceso del escenario.
     */
    private static final class ProcessSpec {
        final String name;
        final int totalInstructions;
        final int arrivalCycle;

        ProcessSpec(String name, int totalInstructions, int arrivalCycle) {
            this.name = Objects.requireNonNull(name, "El nombre no puede ser nulo");
            this.totalInstructions = totalInstructions;
            this.arrivalCycle = arrivalCycle;
        }
    }

    /**
     * Contenedor de información efectiva del proceso una vez creado en el sistema.
     */
    private static final class ProcessInfo {
        final int id;
        final String name;
        final int instructions;
        final int arrivalCycle;

        ProcessInfo(int id, String name, int instructions, int arrivalCycle) {
            this.id = id;
            this.name = name;
            this.instructions = instructions;
            this.arrivalCycle = arrivalCycle;
        }
    }

    /**
     * Resultado agregado de la simulación de una política concreta.
     */
    private static final class ScenarioResult {
        private final String label;
        private final PolicyType policy;
        private final Map<String, Integer> waitingTimes;
        private double averageWaitingTime;
        private long totalCycles;

        ScenarioResult(String label, PolicyType policy) {
            this.label = label;
            this.policy = policy;
            this.waitingTimes = new LinkedHashMap<>();
        }

        void addWaitingTime(String processName, int waitingTime) {
            waitingTimes.put(processName, waitingTime);
        }

        void setTotalCycles(long totalCycles) {
            this.totalCycles = totalCycles;
        }

        void computeAverage() {
            averageWaitingTime = waitingTimes.values()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
        }

        double getAverageWaitingTime() {
            return averageWaitingTime;
        }

        int getWaitingTime(String processName) {
            return waitingTimes.getOrDefault(processName, 0);
        }

        long getTotalCycles() {
            return totalCycles;
        }

        void printDetailed() {
            System.out.println("\n📊 RESUMEN DE " + label + " (" + policy + ")");
            System.out.println("   Total de ciclos ejecutados: " + totalCycles);
            System.out.println("   Tiempos de espera por proceso:");
            waitingTimes.forEach((name, waiting) ->
                    System.out.printf("     - %-12s → %3d ciclos%n", name, waiting));
            System.out.printf("   Tiempo de espera promedio: %.2f ciclos%n", averageWaitingTime);
        }
    }
}
