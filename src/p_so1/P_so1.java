/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package p_so1;

import core.CPU;
import core.OperatingSystem;
import core.ProcessControlBlock;
import scheduler.PolicyType;
import util.IOHandler;
import java.util.Scanner;

/**
 * Simulador Interactivo de Algoritmos de Planificación de Procesos.
 * Permite seleccionar y ejecutar algoritmos de planificación individualmente
 * con escenarios optimizados para demostrar su comportamiento.
 */
public class P_so1 {

    /** Duración de cada ciclo del reloj simulado. */
    private static final long CYCLE_DURATION_MS = 100L; // Visualización más rápida pero legible
    /** Límite de ciclos antes de abortar la simulación. */
    private static final int MAX_CYCLES = 1000;

    /** Escenario optimizado para FCFS: demuestra efecto convoy */
    private static final ProcessSpec[] SCENARIO_FCFS = new ProcessSpec[] {
        new ProcessSpec("P1-Largo", 30, 0),
        new ProcessSpec("P2-Corto", 5, 5),
        new ProcessSpec("P3-Corto", 5, 10)
    };

    /** Escenario optimizado para SPN: demuestra selección de más corto */
    private static final ProcessSpec[] SCENARIO_SPN = new ProcessSpec[] {
        new ProcessSpec("P1-Largo", 25, 0),
        new ProcessSpec("P2-Corto", 3, 5),
        new ProcessSpec("P3-Corto", 4, 10),
        new ProcessSpec("P4-Corto", 2, 15)
    };

    /** Escenario optimizado para SRTF: demuestra expropiación */
    private static final ProcessSpec[] SCENARIO_SRTF = new ProcessSpec[] {
        new ProcessSpec("P1-Largo", 20, 0),
        new ProcessSpec("P2-Corto", 3, 5),
        new ProcessSpec("P3-Medio", 8, 10),
        new ProcessSpec("P4-MuyCorto", 2, 15)
    };

    /** Escenario optimizado para Round Robin: demuestra expropiación por quantum */
    private static final ProcessSpec[] SCENARIO_RR = new ProcessSpec[] {
        new ProcessSpec("P1-CPU-Intensivo", 12, 0),
        new ProcessSpec("P2-I/O-Intensivo", 8, 2),
        new ProcessSpec("P3-Medio", 10, 4),
        new ProcessSpec("P4-Corto", 6, 6)
    };

    /**
     * Punto de entrada principal con menú interactivo.
     * @param args no utilizados
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        imprimirBanner();

        while (continuar) {
            mostrarMenu();
            int opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1:
                    ejecutarEscenario("FCFS (First Come First Served)", PolicyType.FCFS, SCENARIO_FCFS);
                    break;
                case 2:
                    ejecutarEscenario("SPN (Shortest Process Next)", PolicyType.SPN, SCENARIO_SPN);
                    break;
                case 3:
                    ejecutarEscenario("SRTF (Shortest Remaining Time First)", PolicyType.SRT, SCENARIO_SRTF);
                    break;
                case 4:
                    ejecutarEscenario("Round Robin (Quantum=3)", PolicyType.ROUND_ROBIN, SCENARIO_RR);
                    break;
                case 5:
                    continuar = false;
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("❌ Opción no válida. Intente nuevamente.\n");
            }

            if (continuar && opcion >= 1 && opcion <= 4) {
                System.out.println("\nPresione Enter para volver al menú...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private static void imprimirBanner() {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║        SIMULADOR INTERACTIVO DE PLANIFICACIÓN DE PROCESOS     ║");
        System.out.println("║                FCFS • SPN • SRTF • ROUND ROBIN                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        System.out.println("🎯 OBJETIVO: Observar el comportamiento de cada algoritmo en escenarios");
        System.out.println("   optimizados que demuestran sus características principales.\n");
    }

    private static void mostrarMenu() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                     MENÚ PRINCIPAL                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println("1. FCFS - First Come First Served");
        System.out.println("   → Efecto convoy: procesos largos bloquean cortos");
        System.out.println("2. SPN - Shortest Process Next");
        System.out.println("   → Selecciona siempre el proceso más corto disponible");
        System.out.println("3. SRTF - Shortest Remaining Time First");
        System.out.println("   → Expropia cuando llega un proceso con tiempo restante menor");
        System.out.println("4. Round Robin - Expropiación por Quantum");
        System.out.println("   → Turnos equitativos con quantum configurable");
        System.out.println("5. Salir");
        System.out.print("\nSeleccione una opción (1-5): ");
    }

    private static int leerOpcion(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void ejecutarEscenario(String titulo, PolicyType politica, ProcessSpec[] scenario) {
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  EJECUTANDO: " + titulo);
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        // Mostrar configuración del escenario
        System.out.println("📋 CONFIGURACIÓN DEL ESCENARIO:");
        for (ProcessSpec spec : scenario) {
            System.out.printf("   • %s: %d instrucciones, llega en ciclo %d%n",
                    spec.nombre, spec.totalInstrucciones, spec.arribo);
        }
        System.out.println();

        // Ejecutar simulación
        ScenarioResult resultado = simularEscenario(titulo, politica, scenario);

        // Mostrar resultados
        resultado.imprimirDetalle();

        // Análisis específico por política
        analizarPolitica(politica, resultado);
    }

    private static ScenarioResult simularEscenario(String titulo, PolicyType politica, ProcessSpec[] scenario) {
        OperatingSystem os = new OperatingSystem();
        os.setCycleDurationMillis(CYCLE_DURATION_MS);
        IOHandler ioHandler = new IOHandler(os, CYCLE_DURATION_MS);
        CPU cpu = new CPU(os, ioHandler);
        
        // Configurar quantum para Round Robin
        if (politica == PolicyType.ROUND_ROBIN) {
            cpu.setTimeQuantum(3); // Quantum de 3 ciclos para mejor visualización
        }
        
        os.attachCpu(cpu);
        os.setSchedulingPolicy(politica);

        int totalProcesos = scenario.length;
        ProcessInfo[] infos = new ProcessInfo[totalProcesos];
        int[] processIds = new int[totalProcesos];
        boolean[] encolados = new boolean[totalProcesos];

        for (int i = 0; i < totalProcesos; i++) {
            ProcessSpec spec = scenario[i];
            infos[i] = new ProcessInfo(spec.nombre, spec.totalInstrucciones, spec.arribo);
        }

        inicializarArribos(os, infos, processIds, encolados, 0);
        os.startSystemClock();

        System.out.println("🚀 INICIANDO SIMULACIÓN...");
        System.out.println("   (Cada ciclo dura " + CYCLE_DURATION_MS + "ms para mejor visualización)\n");

        while (os.getGlobalClockCycle() < MAX_CYCLES) {
            long cicloActual = os.getGlobalClockCycle();

            // Mostrar estado cada 5 ciclos
            if (cicloActual % 5 == 0) {
                mostrarEstadoActual(os, cpu, cicloActual);
            }

            encolarArribosPendientes(os, cicloActual, infos, processIds, encolados);

            try {
                Thread.sleep(CYCLE_DURATION_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            ProcessControlBlock enEjecucion = cpu.getCurrentProcess();
            if (enEjecucion != null) {
                int indice = buscarIndicePorId(processIds, enEjecucion.getProcessId());
                if (indice >= 0 && infos[indice].cicloInicio < 0) {
                    infos[indice].cicloInicio = (int) cicloActual;
                    System.out.printf("▶️  %s INICIA EJECUCIÓN en ciclo %d%n",
                            infos[indice].nombre, cicloActual);
                }
            }

            if (todosTerminados(os, cpu, encolados)) {
                break;
            }
        }

        os.stopSystemClock();
        ioHandler.stop();

        ScenarioResult resultado = new ScenarioResult(titulo, politica, infos);
        resultado.setTotalCiclos(os.getGlobalClockCycle());
        resultado.calcularTiemposDeEspera();
        return resultado;
    }

    private static void mostrarEstadoActual(OperatingSystem os, CPU cpu, long ciclo) {
        System.out.printf("⏰ Ciclo %d: ", ciclo);
        ProcessControlBlock actual = cpu.getCurrentProcess();
        if (actual != null) {
            System.out.printf("Ejecutando %s (PID=%d)%n", actual.getProcessName(), actual.getProcessId());
        } else {
            System.out.println("CPU inactiva");
        }
    }

    private static void inicializarArribos(OperatingSystem os,
                                           ProcessInfo[] infos,
                                           int[] processIds,
                                           boolean[] encolados,
                                           int cicloReferencia) {
        for (int i = 0; i < infos.length; i++) {
            if (!encolados[i] && infos[i].arribo <= cicloReferencia) {
                encolarProceso(os, infos, processIds, encolados, i);
            }
        }
    }

    private static void encolarArribosPendientes(OperatingSystem os,
                                                 long cicloActual,
                                                 ProcessInfo[] infos,
                                                 int[] processIds,
                                                 boolean[] encolados) {
        for (int i = 0; i < infos.length; i++) {
            if (!encolados[i] && infos[i].arribo <= cicloActual) {
                encolarProceso(os, infos, processIds, encolados, i);
            }
        }
    }

    private static void encolarProceso(OperatingSystem os,
                                       ProcessInfo[] infos,
                                       int[] processIds,
                                       boolean[] encolados,
                                       int indice) {
        ProcessInfo info = infos[indice];
        ProcessControlBlock pcb = new ProcessControlBlock(info.nombre);
        pcb.setTotalInstructions(info.instrucciones);
        pcb.setIOBound(false);
        os.moveToReady(pcb);
        processIds[indice] = pcb.getProcessId();
        info.id = pcb.getProcessId();
        encolados[indice] = true;
        System.out.printf("📥 PROCESO LLEGA: %s (PID=%d, %d instrucciones)%n",
                info.nombre, info.id, info.instrucciones);
    }

    private static int buscarIndicePorId(int[] processIds, int id) {
        for (int i = 0; i < processIds.length; i++) {
            if (processIds[i] == id) {
                return i;
            }
        }
        return -1;
    }

    private static boolean todosTerminados(OperatingSystem os,
                                            CPU cpu,
                                            boolean[] encolados) {
        int encoladosTotal = 0;
        for (int i = 0; i < encolados.length; i++) {
            if (encolados[i]) {
                encoladosTotal++;
            }
        }
        return encoladosTotal == encolados.length
                && os.finishedQueueSize() == encolados.length
                && cpu.isIdle();
    }

    private static void analizarPolitica(PolicyType politica, ScenarioResult resultado) {
        System.out.println("\n🔍 ANÁLISIS DEL ALGORITMO:");
        switch (politica) {
            case FCFS:
                System.out.println("   • FCFS procesa en orden de llegada (FIFO)");
                System.out.println("   • Ventaja: Simple y justo");
                System.out.println("   • Desventaja: Efecto convoy - procesos cortos esperan largos");
                break;
            case SPN:
                System.out.println("   • SPN selecciona el proceso con menos instrucciones totales");
                System.out.println("   • Ventaja: Minimiza tiempo de espera promedio");
                System.out.println("   • Desventaja: Inanición de procesos largos");
                break;
            case SRT:
                System.out.println("   • SRTF expropia si llega un proceso con menos tiempo restante");
                System.out.println("   • Ventaja: Respuesta óptima para procesos cortos");
                System.out.println("   • Desventaja: Más complejo, posible inanición si no se maneja");
                break;
            case ROUND_ROBIN:
                System.out.println("   • Round Robin asigna turnos equitativos con quantum fijo");
                System.out.println("   • Ventaja: Equidad y respuesta predecible");
                System.out.println("   • Desventaja: Overhead de cambio de contexto");
                break;
        }
        System.out.printf("   • Tiempo de espera promedio: %.2f ciclos%n", resultado.obtenerPromedioEspera());
    }

    /** Descriptor de proceso para el escenario de prueba. */
    private static final class ProcessSpec {
        final String nombre;
        final int totalInstrucciones;
        final int arribo;

        ProcessSpec(String nombre, int totalInstrucciones, int arribo) {
            this.nombre = nombre;
            this.totalInstrucciones = totalInstrucciones;
            this.arribo = arribo;
        }
    }

    /** Información dinámica de cada proceso durante la simulación. */
    private static final class ProcessInfo {
        int id;
        final String nombre;
        final int instrucciones;
        final int arribo;
        int cicloInicio;
        int tiempoEspera;

        ProcessInfo(String nombre, int instrucciones, int arribo) {
            this.nombre = nombre;
            this.instrucciones = instrucciones;
            this.arribo = arribo;
            this.id = -1;
            this.cicloInicio = -1;
            this.tiempoEspera = 0;
        }
    }

    /** Resultado agregado para cada política. */
    private static final class ScenarioResult {
        final String etiqueta;
        final PolicyType politica;
        final ProcessInfo[] infos;
        private double promedioEspera;
        private long totalCiclos;

        ScenarioResult(String etiqueta, PolicyType politica, ProcessInfo[] infos) {
            this.etiqueta = etiqueta;
            this.politica = politica;
            this.infos = infos;
            this.promedioEspera = 0.0;
            this.totalCiclos = 0L;
        }

        void setTotalCiclos(long totalCiclos) {
            this.totalCiclos = totalCiclos;
        }

        void calcularTiemposDeEspera() {
            double acumulado = 0.0;
            for (int i = 0; i < infos.length; i++) {
                ProcessInfo info = infos[i];
                if (info.cicloInicio < 0) {
                    info.cicloInicio = (int) totalCiclos;
                }
                info.tiempoEspera = info.cicloInicio - info.arribo;
                if (info.tiempoEspera < 0) {
                    info.tiempoEspera = 0;
                }
                acumulado += info.tiempoEspera;
            }
            promedioEspera = infos.length == 0 ? 0.0 : acumulado / infos.length;
        }

        double obtenerPromedioEspera() {
            return promedioEspera;
        }

        int obtenerEsperaProceso(String nombre) {
            for (int i = 0; i < infos.length; i++) {
                if (infos[i].nombre.equals(nombre)) {
                    return infos[i].tiempoEspera;
                }
            }
            return 0;
        }

        void imprimirDetalle() {
            System.out.println("\n📊 RESULTADOS FINALES:");
            System.out.printf("   Política: %s%n", etiqueta);
            System.out.printf("   Total de ciclos ejecutados: %d%n", totalCiclos);
            System.out.println("   ┌─ Tiempos de espera por proceso ─┐");
            for (int i = 0; i < infos.length; i++) {
                ProcessInfo info = infos[i];
                String barra = "█".repeat(Math.max(0, info.tiempoEspera / 2))
                            + "░".repeat(Math.max(0, 15 - (info.tiempoEspera / 2)));
                System.out.printf("   │ %-12s %3d ciclos │%s│%n",
                        info.nombre,
                        info.tiempoEspera,
                        barra);
            }
            System.out.println("   └─────────────────────────────────┘");
            System.out.printf("   Tiempo de espera promedio: %.2f ciclos%n", promedioEspera);
        }
    }
}
