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
import java.text.MessageFormat;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Simulador Interactivo de Algoritmos de Planificación de Procesos.
 * Permite seleccionar y ejecutar algoritmos de planificación individualmente
 * con escenarios optimizados para demostrar su comportamiento.
 */
public class P_so1 {

    /** Duración de cada ciclo del reloj simulado. */
    private static final long CYCLE_DURATION_MS = 100L;
    /** Límite de ciclos antes de abortar la simulación. */
    private static final int MAX_CYCLES = 1000;
    /** Quantum a utilizar para Round Robin. */
    private static final int ROUND_ROBIN_QUANTUM = 3;

    /** Código ANSI para reiniciar el color. */
    private static final String ANSI_RESET = "\u001B[0m";
    /** Color asociado a los ciclos del reloj. */
    private static final String COLOR_CYCLE = "\u001B[36m";
    /** Color asociado a cambios de estado registrados por el sistema. */
    private static final String COLOR_STATE = "\u001B[35m";
    /** Color asociado a eventos de arribo de procesos. */
    private static final String COLOR_PROCESS = "\u001B[32m";
    /** Color asociado a cambios de proceso en CPU. */
    private static final String COLOR_SWITCH = "\u001B[96m";
    /** Color asociado al estado de las colas. */
    private static final String COLOR_QUEUE = "\u001B[33m";
    /** Color dedicado al resumen final de métricas. */
    private static final String COLOR_SUMMARY = "\u001B[34m";
    /** Color dedicado a eventos de I/O. */
    private static final String COLOR_IO = "\u001B[94m";
    /** Color para mensajes de alerta o errores. */
    private static final String COLOR_ERROR = "\u001B[31m";

    /** Escenario optimizado para FCFS: resalta el efecto convoy y suspensiones por memoria. */
    private static final ProcessSpec[] SCENARIO_FCFS = new ProcessSpec[] {
        new ProcessSpec("FCFS-Largo", 32, 0, false, -1, 0),
        new ProcessSpec("FCFS-IO-A", 14, 0, true, 4, 3),
        new ProcessSpec("FCFS-Corto", 6, 1, false, -1, 0),
        new ProcessSpec("FCFS-IO-B", 9, 1, true, 3, 2),
        new ProcessSpec("FCFS-Extra-1", 8, 2, false, -1, 0),
        new ProcessSpec("FCFS-Extra-2", 7, 2, false, -1, 0)
    };

    /** Escenario optimizado para SPN: enfatiza la selección del proceso más corto disponible. */
    private static final ProcessSpec[] SCENARIO_SPN = new ProcessSpec[] {
        new ProcessSpec("SPN-Base", 20, 0, false, -1, 0),
        new ProcessSpec("SPN-Corto-IO", 5, 0, true, 2, 2),
        new ProcessSpec("SPN-Flash", 3, 1, false, -1, 0),
        new ProcessSpec("SPN-Rapido", 4, 2, false, -1, 0),
        new ProcessSpec("SPN-Med-IO", 7, 2, true, 2, 3),
        new ProcessSpec("SPN-Largo", 18, 3, false, -1, 0)
    };

    /** Escenario optimizado para HRRN: procesos cortos llegan tras esperas prolongadas. */
    private static final ProcessSpec[] SCENARIO_HRRN = new ProcessSpec[] {
        new ProcessSpec("HRRN-Largo", 28, 0, false, -1, 0),
        new ProcessSpec("HRRN-Medio", 12, 1, false, -1, 0),
        new ProcessSpec("HRRN-Corto-A", 5, 6, false, -1, 0),
        new ProcessSpec("HRRN-Corto-B", 4, 10, false, -1, 0),
        new ProcessSpec("HRRN-Corto-C", 3, 14, false, -1, 0)
    };

    /** Escenario optimizado para SRTF: demuestra expropiaciones por tiempo restante. */
    private static final ProcessSpec[] SCENARIO_SRTF = new ProcessSpec[] {
        new ProcessSpec("SRTF-Largo", 30, 0, false, -1, 0),
        new ProcessSpec("SRTF-IO-1", 11, 1, true, 3, 3),
        new ProcessSpec("SRTF-Flash", 3, 2, false, -1, 0),
        new ProcessSpec("SRTF-Medio", 12, 2, false, -1, 0),
        new ProcessSpec("SRTF-IO-2", 8, 3, true, 2, 2),
        new ProcessSpec("SRTF-Extra", 5, 3, false, -1, 0)
    };

    /** Escenario optimizado para Round Robin: ilustra repartición equitativa y bloqueos. */
    private static final ProcessSpec[] SCENARIO_RR = new ProcessSpec[] {
        new ProcessSpec("RR-CPU-Pesado", 22, 0, false, -1, 0),
        new ProcessSpec("RR-IO-1", 13, 0, true, 4, 3),
        new ProcessSpec("RR-Medio", 12, 1, false, -1, 0),
        new ProcessSpec("RR-IO-2", 10, 1, true, 3, 2),
        new ProcessSpec("RR-Ligero", 6, 2, false, -1, 0),
        new ProcessSpec("RR-Refuerzo", 8, 2, false, -1, 0)
    };

    /**
     * Punto de entrada principal con menú interactivo.
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        configurarSalidaColoreada();
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
                    ejecutarEscenario("HRRN (Highest Response Ratio Next)", PolicyType.HRRN, SCENARIO_HRRN);
                    break;
                case 4:
                    ejecutarEscenario("SRTF (Shortest Remaining Time First)", PolicyType.SRT, SCENARIO_SRTF);
                    break;
                case 5:
                    ejecutarEscenario("Round Robin (Quantum=3)", PolicyType.ROUND_ROBIN, SCENARIO_RR);
                    break;
                case 6:
                    continuar = false;
                    imprimirConColor(COLOR_SUMMARY, "¡Hasta luego!");
                    break;
                default:
                    imprimirConColor(COLOR_ERROR, "❌ Opción no válida. Intente nuevamente.\n");
            }

            if (continuar && opcion >= 1 && opcion <= 5) {
                imprimirConColor(COLOR_QUEUE, "\nPresione Enter para volver al menú...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    /**
     * Configura el logger global para imprimir eventos con códigos de color diferenciados.
     */
    private static void configurarSalidaColoreada() {
        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }
        ColoredConsoleHandler handler = new ColoredConsoleHandler();
        handler.setLevel(Level.INFO);
        rootLogger.addHandler(handler);
        rootLogger.setLevel(Level.INFO);
    }

    /**
     * Devuelve el mensaje decorado con el color indicado.
     * @param color código ANSI a aplicar
     * @param mensaje texto original a colorear
     * @return texto coloreado
     */
    private static String colorear(String color, String mensaje) {
        return color + mensaje + ANSI_RESET;
    }

    /**
     * Imprime un mensaje en consola aplicando el color especificado.
     * @param color código ANSI a aplicar
     * @param mensaje texto a mostrar
     */
    private static void imprimirConColor(String color, String mensaje) {
        System.out.println(colorear(color, mensaje));
    }

    /**
     * Muestra el banner inicial del simulador con información general.
     */
    private static void imprimirBanner() {
        imprimirConColor(COLOR_SUMMARY, "╔════════════════════════════════════════════════════════════════╗");
        imprimirConColor(COLOR_SUMMARY, "║        SIMULADOR INTERACTIVO DE PLANIFICACIÓN DE PROCESOS     ║");
        imprimirConColor(COLOR_SUMMARY, "║          FCFS • SPN • HRRN • SRTF • ROUND ROBIN               ║");
        imprimirConColor(COLOR_SUMMARY, "╚════════════════════════════════════════════════════════════════╝\n");
        imprimirConColor(COLOR_SUMMARY, "🎯 OBJETIVO: Observar el comportamiento de cada algoritmo en escenarios");
        imprimirConColor(COLOR_SUMMARY, "   optimizados que demuestran sus características principales.\n");
    }

    /**
     * Despliega el menú principal con las políticas disponibles.
     */
    private static void mostrarMenu() {
        imprimirConColor(COLOR_QUEUE, "╔══════════════════════════════════════════════════════════════╗");
        imprimirConColor(COLOR_QUEUE, "║                     MENÚ PRINCIPAL                           ║");
        imprimirConColor(COLOR_QUEUE, "╚══════════════════════════════════════════════════════════════╝");
        System.out.println("1. FCFS - First Come First Served");
        System.out.println("   → Efecto convoy: procesos largos bloquean cortos");
        System.out.println("2. SPN - Shortest Process Next");
        System.out.println("   → Selecciona siempre el proceso más corto disponible");
        System.out.println("3. HRRN - Highest Response Ratio Next");
        System.out.println("   → Balancea espera y duración, evita inanición prolongada");
        System.out.println("4. SRTF - Shortest Remaining Time First");
        System.out.println("   → Expropia cuando llega un proceso con tiempo restante menor");
        System.out.println("5. Round Robin - Expropiación por Quantum");
        System.out.println("   → Turnos equitativos con quantum configurable");
        System.out.println("6. Salir");
        System.out.print("\nSeleccione una opción (1-6): ");
    }

    /**
     * Lee la opción seleccionada por el usuario validando valores numéricos.
     * @param scanner lector compartido de entradas
     * @return número de opción o -1 si es inválida
     */
    private static int leerOpcion(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Ejecuta la simulación asociada a la política seleccionada mostrando configuración y resultados.
     * @param titulo etiqueta amigable de la política
     * @param politica política de planificación a evaluar
     * @param scenario arreglo de procesos precalibrados
     */
    private static void ejecutarEscenario(String titulo, PolicyType politica, ProcessSpec[] scenario) {
        imprimirConColor(COLOR_SUMMARY, "\n═══════════════════════════════════════════════════════════════");
        imprimirConColor(COLOR_SUMMARY, "  EJECUTANDO: " + titulo);
        imprimirConColor(COLOR_SUMMARY, "═══════════════════════════════════════════════════════════════\n");

        System.out.println("📋 CONFIGURACIÓN DEL ESCENARIO:");
        for (ProcessSpec spec : scenario) {
            String ioInfo = spec.cicloIO >= 0
                    ? String.format(" (I/O en ciclo %d por %d ciclos)", spec.cicloIO, spec.duracionIO)
                    : "";
            System.out.printf("   • %s: %d instrucciones, llega en ciclo %d%s%n",
                    spec.nombre,
                    spec.totalInstrucciones,
                    spec.arribo,
                    ioInfo);
        }
        System.out.println();

        ScenarioResult resultado = simularEscenario(titulo, politica, scenario);
        resultado.imprimirDetalle();
        analizarPolitica(politica, resultado);
    }

    /**
     * Ejecuta la simulación en tiempo real controlando llegadas, bloqueos, suspensiones y métricas.
     * @param titulo etiqueta amigable del escenario
     * @param politica política a emplear
     * @param scenario definición de procesos para el escenario
     * @return resultado con métricas agregadas del escenario
     */
    private static ScenarioResult simularEscenario(String titulo, PolicyType politica, ProcessSpec[] scenario) {
        OperatingSystem os = new OperatingSystem();
        os.setCycleDurationMillis(CYCLE_DURATION_MS);
        os.setRoundRobinQuantum(ROUND_ROBIN_QUANTUM);
        IOHandler ioHandler = new IOHandler(os, CYCLE_DURATION_MS);
        Thread ioThread = new Thread(ioHandler, "IOHandler-" + politica.name());
        ioThread.setDaemon(true);
        ioThread.start();
        CPU cpu = new CPU(os, ioHandler);
        os.attachCpu(cpu);
        os.setSchedulingPolicy(politica);

        int totalProcesos = scenario.length;
        ProcessInfo[] infos = new ProcessInfo[totalProcesos];
        int[] processIds = new int[totalProcesos];
        boolean[] encolados = new boolean[totalProcesos];

        for (int i = 0; i < totalProcesos; i++) {
            ProcessSpec spec = scenario[i];
            infos[i] = new ProcessInfo(spec.nombre, spec.totalInstrucciones, spec.arribo, spec.ioBound, spec.cicloIO, spec.duracionIO);
        }

        inicializarArribos(os, infos, processIds, encolados, 0);
        os.startSystemClock();

        imprimirConColor(COLOR_CYCLE, "🚀 INICIANDO SIMULACIÓN...");
        imprimirConColor(COLOR_CYCLE, "   (Cada ciclo dura " + CYCLE_DURATION_MS + "ms para mejor visualización)\n");

        int lastBlockedSize = -1;
        int lastBlockedSuspSize = -1;
        int lastReadySuspSize = -1;
        int ultimoPid = Integer.MIN_VALUE;

        while (os.getGlobalClockCycle() < MAX_CYCLES) {
            long cicloActual = os.getGlobalClockCycle();

            ProcessControlBlock procesoActual = cpu.getCurrentProcess();
            int pidActual = procesoActual != null ? procesoActual.getProcessId() : -1;
            if (pidActual != ultimoPid) {
                if (procesoActual != null) {
                    String mensaje = String.format("🔄 Cambio de CPU → %s (PID=%d)",
                            procesoActual.getProcessName(),
                            procesoActual.getProcessId());
                    imprimirConColor(COLOR_SWITCH, mensaje);
                } else if (ultimoPid != -1 && ultimoPid != Integer.MIN_VALUE) {
                    imprimirConColor(COLOR_SWITCH, "🛑 La CPU queda inactiva");
                }
                ultimoPid = pidActual;
            }

            if (cicloActual % 5 == 0) {
                mostrarEstadoActual(os, cpu, cicloActual);
            }

            encolarArribosPendientes(os, cicloActual, infos, processIds, encolados);

            int blockedSize = os.blockedQueueSize();
            int blockedSuspSize = os.blockedSuspendedQueueSize();
            int readySuspSize = os.readySuspendedQueueSize();
            if (blockedSize != lastBlockedSize || blockedSuspSize != lastBlockedSuspSize || readySuspSize != lastReadySuspSize) {
                String mensaje = String.format("   🔒 Bloqueados: %d | Suspendidos bloqueados: %d | Ready suspendidos: %d",
                        blockedSize,
                        blockedSuspSize,
                        readySuspSize);
                imprimirConColor(COLOR_IO, mensaje);
                lastBlockedSize = blockedSize;
                lastBlockedSuspSize = blockedSuspSize;
                lastReadySuspSize = readySuspSize;
            }

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
                    String mensaje = String.format("▶️  %s INICIA EJECUCIÓN en ciclo %d",
                            infos[indice].nombre,
                            cicloActual);
                    imprimirConColor(COLOR_PROCESS, mensaje);
                }
            }

            if (todosTerminados(os, cpu, encolados)) {
                break;
            }
        }

        os.stopSystemClock();
        ioHandler.stop();
        try {
            ioThread.join(500L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        ScenarioResult resultado = new ScenarioResult(titulo, politica, infos);
        resultado.setTotalCiclos(os.getGlobalClockCycle());
        resultado.calcularTiemposDeEspera();
        return resultado;
    }

    /**
     * Muestra un resumen del estado actual del sistema cada ciertos ciclos.
     * @param os referencia al sistema operativo
     * @param cpu referencia a la CPU
     * @param ciclo número de ciclo global
     */
    private static void mostrarEstadoActual(OperatingSystem os, CPU cpu, long ciclo) {
        int enMemoria = os.getProcessesInMemory();
        int ready = os.readyQueueSize();
        int blocked = os.blockedQueueSize();
        int readySusp = os.readySuspendedQueueSize();
        int blockedSusp = os.blockedSuspendedQueueSize();
        int finished = os.finishedQueueSize();

        String encabezado = String.format("⏰ Ciclo %d | Memoria: %d/4", ciclo, enMemoria);
        imprimirConColor(COLOR_CYCLE, encabezado);

        ProcessControlBlock actual = cpu.getCurrentProcess();
        if (actual != null) {
            String mensaje = String.format("   CPU ejecuta %s (PID=%d, PC=%d/%d)",
                    actual.getProcessName(),
                    actual.getProcessId(),
                    actual.getProgramCounter(),
                    actual.getTotalInstructions());
            imprimirConColor(COLOR_PROCESS, mensaje);
        } else {
            imprimirConColor(COLOR_PROCESS, "   CPU inactiva");
        }

        String colas = String.format("   Ready: %d | Blocked: %d | Ready-Susp: %d | Blocked-Susp: %d | Finished: %d",
                ready,
                blocked,
                readySusp,
                blockedSusp,
                finished);
        imprimirConColor(COLOR_QUEUE, colas);
    }

    /**
     * Encola procesos cuya llegada ocurre antes o igual al ciclo de referencia inicial.
     * @param os sistema operativo responsable de las colas
     * @param infos metadatos de los procesos
     * @param processIds arreglo para mapear índices con PID
     * @param encolados bandera de arribo por proceso
     * @param cicloReferencia ciclo utilizado para validar arribos iniciales
     */
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

    /**
     * Encola procesos cuya llegada está programada para el ciclo actual.
     * @param os sistema operativo responsable de las colas
     * @param cicloActual ciclo global vigente
     * @param infos metadatos de los procesos
     * @param processIds arreglo para mapear índices con PID
     * @param encolados bandera de arribo por proceso
     */
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

    /**
     * Crea el PCB del proceso indicado y lo mueve a la cola de listos respetando la configuración I/O.
     * @param os sistema operativo responsable de la transición
     * @param infos metadatos de los procesos
     * @param processIds arreglo para mapear índices con PID
     * @param encolados bandera de arribo por proceso
     * @param indice posición del proceso en el escenario
     */
    private static void encolarProceso(OperatingSystem os,
                                       ProcessInfo[] infos,
                                       int[] processIds,
                                       boolean[] encolados,
                                       int indice) {
        ProcessInfo info = infos[indice];
        ProcessControlBlock pcb = new ProcessControlBlock(info.nombre);
        pcb.setTotalInstructions(info.instrucciones);
        pcb.setIOBound(info.ioBound);
        pcb.setIoExceptionCycle(info.cicloIO);
        pcb.setIoDuration(info.duracionIO);
        os.moveToReady(pcb);
        processIds[indice] = pcb.getProcessId();
        info.id = pcb.getProcessId();
        encolados[indice] = true;
        String llegada = String.format("📥 PROCESO LLEGA: %s (PID=%d, %d instrucciones)",
                info.nombre,
                info.id,
                info.instrucciones);
        imprimirConColor(COLOR_PROCESS, llegada);
        if (info.cicloIO >= 0) {
            String ioMensaje = String.format("   ↳ Solicitará I/O en ciclo local %d por %d ciclos",
                    info.cicloIO,
                    info.duracionIO);
            imprimirConColor(COLOR_IO, ioMensaje);
        }
    }

    /**
     * Busca el índice asociado a un PID específico dentro del arreglo de procesos.
     * @param processIds arreglo de PIDs
     * @param id PID buscado
     * @return índice del proceso o -1 si no existe
     */
    private static int buscarIndicePorId(int[] processIds, int id) {
        for (int i = 0; i < processIds.length; i++) {
            if (processIds[i] == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determina si todos los procesos ya finalizaron considerando colas y estado de la CPU.
     * @param os sistema operativo con la información de colas
     * @param cpu CPU ejecutando los procesos
     * @param encolados bandera de arribo por proceso
     * @return true cuando todos los procesos terminaron
     */
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

    /**
     * Analiza las ventajas y desventajas de la política ejecutada mostrando sus métricas principales.
     * @param politica política evaluada
     * @param resultado métricas agregadas del escenario
     */
    private static void analizarPolitica(PolicyType politica, ScenarioResult resultado) {
        imprimirConColor(COLOR_SUMMARY, "\n🔍 ANÁLISIS DEL ALGORITMO:");
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
            case HRRN:
                System.out.println("   • HRRN pondera tiempo de espera y duración total");
                System.out.println("   • Ventaja: Procesos largos incrementan prioridad mientras esperan");
                System.out.println("   • Desventaja: Requiere cálculo adicional por selección");
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
        final boolean ioBound;
        final int cicloIO;
        final int duracionIO;

        /**
         * Construye un descriptor de proceso con su comportamiento esperado.
         * @param nombre nombre visible del proceso
         * @param totalInstrucciones total de instrucciones que ejecutará
         * @param arribo ciclo en el que llega al sistema
         * @param ioBound marca si el proceso provoca I/O
         * @param cicloIO ciclo local en el que lanzará la petición de I/O (-1 si no aplica)
         * @param duracionIO cantidad de ciclos que permanecerá bloqueado por I/O
         */
        ProcessSpec(String nombre, int totalInstrucciones, int arribo, boolean ioBound, int cicloIO, int duracionIO) {
            this.nombre = nombre;
            this.totalInstrucciones = totalInstrucciones;
            this.arribo = arribo;
            this.ioBound = ioBound;
            this.cicloIO = cicloIO;
            this.duracionIO = duracionIO;
        }
    }

    /** Información dinámica de cada proceso durante la simulación. */
    private static final class ProcessInfo {
        int id;
        final String nombre;
        final int instrucciones;
        final int arribo;
        final boolean ioBound;
        final int cicloIO;
        final int duracionIO;
        int cicloInicio;
        int tiempoEspera;

        /**
         * Construye el contenedor de métricas dinámicas de un proceso durante el escenario.
         * @param nombre etiqueta del proceso
         * @param instrucciones total de instrucciones a ejecutar
         * @param arribo ciclo en el que llega
         * @param ioBound indica si provoca bloqueos por I/O
         * @param cicloIO ciclo local en el que dispara I/O (-1 si no aplica)
         * @param duracionIO duración esperada del bloqueo por I/O
         */
        ProcessInfo(String nombre, int instrucciones, int arribo, boolean ioBound, int cicloIO, int duracionIO) {
            this.nombre = nombre;
            this.instrucciones = instrucciones;
            this.arribo = arribo;
            this.ioBound = ioBound;
            this.cicloIO = cicloIO;
            this.duracionIO = duracionIO;
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

        /**
         * Construye la estructura de resultados para un escenario ejecutado.
         * @param etiqueta nombre descriptivo del escenario
         * @param politica política de planificación aplicada
         * @param infos arreglo de métricas por proceso
         */
        ScenarioResult(String etiqueta, PolicyType politica, ProcessInfo[] infos) {
            this.etiqueta = etiqueta;
            this.politica = politica;
            this.infos = infos;
            this.promedioEspera = 0.0;
            this.totalCiclos = 0L;
        }

        /**
         * Registra la cantidad total de ciclos ejecutados durante la simulación.
         * @param totalCiclos ciclos acumulados por el reloj global
         */
        void setTotalCiclos(long totalCiclos) {
            this.totalCiclos = totalCiclos;
        }

        /**
         * Calcula el tiempo de espera individual y promedio de todos los procesos.
         */
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

        /**
         * Devuelve el tiempo de espera promedio calculado para el escenario.
         * @return tiempo de espera medio en ciclos
         */
        double obtenerPromedioEspera() {
            return promedioEspera;
        }

        /**
         * Imprime el detalle tabla de tiempos de espera y métricas clave del escenario.
         */
        void imprimirDetalle() {
            imprimirConColor(COLOR_SUMMARY, "\n📊 RESULTADOS FINALES:");
            System.out.printf("   Política: %s%n", etiqueta);
            System.out.printf("   Total de ciclos ejecutados: %d%n", totalCiclos);
            System.out.println("   ┌─ Tiempos de espera por proceso ─┐");
            for (int i = 0; i < infos.length; i++) {
                ProcessInfo info = infos[i];
                String barra = "█".repeat(Math.max(0, info.tiempoEspera / 2))
                            + "░".repeat(Math.max(0, 15 - (info.tiempoEspera / 2)));
                String linea = String.format("   │ %-14s espera=%3d ciclos | llegada=%3d | inicio=%3d │%s│",
                        info.nombre,
                        info.tiempoEspera,
                        info.arribo,
                        info.cicloInicio,
                        barra);
                System.out.println(colorear(COLOR_SUMMARY, linea));
            }
            System.out.println("   └─────────────────────────────────┘");
            System.out.printf("   Tiempo de espera promedio: %.2f ciclos%n", promedioEspera);
        }
    }

    /**
     * Handler personalizado para colorear los logs del sistema operativo y la CPU.
     */
    private static final class ColoredConsoleHandler extends Handler {

        /**
         * Procesa cada registro del logger aplicando un color contextual.
         * @param record evento registrado por el logger
         */
        @Override
        public void publish(LogRecord record) {
            if (!isLoggable(record)) {
                return;
            }
            String mensaje = construirMensaje(record);
            String color = seleccionarColor(record, mensaje);
            String prefijo = String.format("[%s]", record.getLoggerName());
            System.out.println(colorear(color, prefijo + " " + mensaje));
            if (record.getThrown() != null) {
                record.getThrown().printStackTrace(System.out);
            }
        }

        /**
         * No se requiere vaciado manual porque la salida es directa en consola.
         */
        @Override
        public void flush() {
            // No hay recursos por vaciar porque escribimos directamente en System.out
        }

        /**
         * No libera recursos adicionales porque el handler no mantiene conexiones externas.
         */
        @Override
        public void close() {
            // Sin recursos adicionales que cerrar
        }

        /**
         * Determina el color adecuado para el mensaje según su origen o contenido.
         * @param record registro original del logger
         * @param mensaje texto formateado del registro
         * @return código de color a aplicar
         */
        private String seleccionarColor(LogRecord record, String mensaje) {
            if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
                return COLOR_ERROR;
            }
            if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                return COLOR_QUEUE;
            }
            String origen = record.getLoggerName();
            if (mensaje.contains("->")) {
                return COLOR_STATE;
            }
            if (mensaje.contains("I/O") || origen.contains("IOHandler")) {
                return COLOR_IO;
            }
            if (mensaje.contains("Ciclo global")) {
                return COLOR_CYCLE;
            }
            if (mensaje.contains("cargado en CPU") || mensaje.contains("libera proceso")) {
                return COLOR_SWITCH;
            }
            return COLOR_QUEUE;
        }

        /**
         * Construye el mensaje final aplicando parámetros y preservando la excepción si existe.
         * @param record evento original del logger
         * @return mensaje listo para impresión
         */
        private String construirMensaje(LogRecord record) {
            String mensaje = record.getMessage();
            Object[] parametros = record.getParameters();
            if (mensaje != null && parametros != null && parametros.length > 0) {
                try {
                    mensaje = MessageFormat.format(mensaje, parametros);
                } catch (IllegalArgumentException ex) {
                    // Se ignora el fallo de formato y se usa el mensaje base
                }
            }
            return mensaje != null ? mensaje : "";
        }
    }
}
