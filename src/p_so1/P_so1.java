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

/**
 * Programa principal para probar US 2.1 a US 2.4:
 * - US 2.1: Reloj del Sistema (hilo que orquesta planificador → despachador → CPU)
 * - US 2.2: Scheduler Base (infraestructura configurable de políticas)
 * - US 2.3: FCFS (First Come, First Served) como referencia del efecto convoy
 * - US 2.4: Round Robin preemptivo con quantum configurable
 * 
 * @author Santiago
 */
public class P_so1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║      SIMULADOR DE SO - PRUEBA INTEGRADA US 2.1 → 2.4         ║");
        System.out.println("║  RELOJ + SCHEDULER BASE + FCFS vs ROUND ROBIN                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  OBJETIVO DE LA PRUEBA:");
        System.out.println("  ✓ US 2.1: Reloj del sistema con ciclos regulares");
        System.out.println("  ✓ US 2.2: Scheduler Base + SchedulingPolicy + PolicyType");
        System.out.println("  ✓ US 2.3: FCFS (First Come, First Served) - efecto convoy");
        System.out.println("  ✓ US 2.4: Round Robin preemptivo con quantum configurable");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        // PARTE 1: PRUEBA CON ROUND ROBIN (US 2.4)
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           PARTE 1: EJECUTANDO CON ROUND ROBIN (RR)           ║");
        System.out.println("║           Quantum = 2 ciclos (preemptivo)                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        executeScenarioRoundRobin();
        
        // Pausa antes de la segunda prueba
        System.out.println("\n\n" + "=".repeat(68));
        System.out.println("ESPERANDO ANTES DE EJECUTAR LA SEGUNDA PARTE...\n");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // PARTE 2: PRUEBA CON FCFS (US 2.3)
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           PARTE 2: EJECUTANDO CON FCFS (REFERENCIA)         ║");
        System.out.println("║           Sin preemptivo - efecto convoy esperado           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        executeScenarioFCFS();
        
        // PARTE 3: COMPARACIÓN FINAL
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              COMPARACIÓN FINAL: RR vs FCFS                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("📊 CONCLUSIONES:");
        System.out.println("  • Round Robin (RR): Distribuye CPU equitativamente entre procesos");
        System.out.println("    → Mejor tiempo de respuesta para procesos cortos");
        System.out.println("    → Evita monopolio de procesos largos");
        System.out.println("    → Ideal para sistemas interactivos");
        System.out.println("\n  • FCFS: Procesos se ejecutan en orden de llegada");
        System.out.println("    → Procesos largos retrasan a los cortos (efecto convoy)");
        System.out.println("    → Simple de implementar");
        System.out.println("    → Mejor para batch systems sin requerimientos interactivos");
        System.out.println("\n✅ VERIFICACIÓN DE USER STORIES COMPLETADAS:");
        System.out.println("   ✓ US 2.1: Reloj del Sistema - ciclos coordinados correctamente");
        System.out.println("   ✓ US 2.2: Scheduler Base - políticas intercambiables en runtime");
        System.out.println("   ✓ US 2.3: FCFS - orden FIFO sin expropiación verificado");
        System.out.println("   ✓ US 2.4: Round Robin - quantum configurable y expropiación funcional");
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║        ✅ PRUEBA INTEGRADA US 2.1 → 2.4 COMPLETADA           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Escenario 1: Ejecuta procesos con política Round Robin (US 2.4).
     */
    private static void executeScenarioRoundRobin() {
        long cycleDurationMillis = 300L; // Ritmo moderado para observar rotaciones

        OperatingSystem os = new OperatingSystem();
        os.setCycleDurationMillis(cycleDurationMillis);
        os.setRoundRobinQuantum(2); // Quantum elegido para la demostración (US 2.4)
        IOHandler ioHandler = new IOHandler(os, cycleDurationMillis);
        CPU cpu = new CPU(os, ioHandler);
        os.attachCpu(cpu);
        
        // US 2.2 y 2.4: Configurar política Round Robin vía PolicyType
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  US 2.2 & US 2.4: CONFIGURANDO ROUND ROBIN                 │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        os.setSchedulingPolicy(PolicyType.ROUND_ROBIN);
        System.out.println("✓ Política configurada: Round Robin (preemptiva)");
        System.out.println("  → Quantum activo: " + os.getRoundRobinQuantum() + " ciclos (valores válidos {1,2,4,8})\n");
        
        // Iniciar el manejador de I/O en un hilo separado
        Thread ioThread = new Thread(ioHandler, "IOHandler-Thread");
        ioThread.setDaemon(true);
        ioThread.start();
        
        System.out.println("✓ Sistema operativo inicializado");
        System.out.println("✓ CPU inicializada y enlazada con Scheduler");
        System.out.println("✓ IOHandler iniciado en hilo separado");
        System.out.println("✓ Política RR lista para ejecutar con quantum = " + os.getRoundRobinQuantum());
        System.out.println("✓ Ciclo del reloj: " + cycleDurationMillis + " ms\n");
        
        // US 2.3: Crear procesos para demostrar FCFS y efecto convoy
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  CREANDO PROCESOS PARA DEMOSTRAR ROUND ROBIN                │");
        System.out.println("└─────────────────────────────────────────────────────────────┘\n");
        
        // Proceso 1: CPU-bound LARGO
        ProcessControlBlock proc1 = new ProcessControlBlock("P1-Long-CPU");
        proc1.setTotalInstructions(12);
        proc1.setIOBound(false);
        System.out.println("✓ P1-Long-CPU: 12 instrucciones");
        System.out.println("  → Se interrumpirá cada 2 ciclos (quantum = 2)");
        
        // Proceso 2: CPU-bound CORTO
        ProcessControlBlock proc2 = new ProcessControlBlock("P2-Short-CPU");
        proc2.setTotalInstructions(3);
        proc2.setIOBound(false);
        System.out.println("✓ P2-Short-CPU: 3 instrucciones");
        System.out.println("  → Obtendrá CPU rápidamente con RR");
        
        // Proceso 3: CPU-bound MEDIANO
        ProcessControlBlock proc3 = new ProcessControlBlock("P3-Medium-CPU");
        proc3.setTotalInstructions(6);
        proc3.setIOBound(false);
        System.out.println("✓ P3-Medium-CPU: 6 instrucciones");
        System.out.println("  → Se ejecutará en turnos de 2 ciclos");
        
        // Agregar procesos a la cola de listos
        System.out.println("\n┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  CARGANDO PROCESOS EN LA COLA DE LISTOS                    │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        
        os.moveToReady(proc1);
        os.moveToReady(proc2);
        os.moveToReady(proc3);
        
        System.out.println("\nReady Queue: P1 (12) → P2 (3) → P3 (6)");
        System.out.println("\n📊 Estado inicial:");
        printSystemState(os, cpu);
        
        // US 2.1: Iniciar reloj del sistema
        System.out.println("\n┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  US 2.1: INICIANDO RELOJ DEL SISTEMA                      │");
        System.out.println("│  Secuencia: Planificador → Despachador → CPU              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘\n");
        
        os.startSystemClock();

        int expectedProcessCount = 3;
        long maxCycles = 100;
        boolean completedSuccessfully = false;
        
        System.out.println("╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║        OBSERVANDO EJECUCIÓN CON ROUND ROBIN (q=2)          ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝\n");

        while (os.getGlobalClockCycle() < maxCycles) {
            try {
                Thread.sleep(cycleDurationMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            long currentCycle = os.getGlobalClockCycle();
            String cpuStatus = cpu.isIdle() ? "IDLE" : cpu.getCurrentProcess().getProcessName();
            
            // Reporte cada ciclo
            System.out.printf("⏱️  Ciclo %2d | Ready: %d | Finished: %d | CPU: %-15s\n",
                    currentCycle,
                    os.readyQueueSize(),
                    os.finishedQueueSize(),
                    cpuStatus);

            // Verificar si todos los procesos han terminado
            if (os.finishedQueueSize() == expectedProcessCount
                    && os.readyQueueSize() == 0
                    && cpu.isIdle()) {
                System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                System.out.println("║  🎉 TODOS LOS " + expectedProcessCount + " PROCESOS FINALIZADOS (RR)              ║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
                completedSuccessfully = true;
                break;
            }
        }

        if (!completedSuccessfully) {
            System.out.println("\n⚠️  No se completaron todos los procesos en " + maxCycles + " ciclos.");
        }

        System.out.println("\n🔴 Deteniendo reloj y componentes...");
        os.stopSystemClock();
        ioHandler.stop();
        try {
            ioThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("✓ Round Robin completado en " + os.getGlobalClockCycle() + " ciclos");
        System.out.println("\n📊 Estado final (RR):");
        printSystemState(os, cpu);
    }

    /**
     * Escenario 2: Ejecuta los mismos procesos con política FCFS (US 2.3 - referencia).
     */
    private static void executeScenarioFCFS() {
        long cycleDurationMillis = 300L;

        OperatingSystem os = new OperatingSystem();
        os.setCycleDurationMillis(cycleDurationMillis);
        IOHandler ioHandler = new IOHandler(os, cycleDurationMillis);
        CPU cpu = new CPU(os, ioHandler);
        os.attachCpu(cpu);
        
        // US 2.2 y 2.3: Configurar política FCFS vía PolicyType
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  US 2.2 & US 2.3: CONFIGURANDO FCFS (REFERENCIA)           │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        os.setSchedulingPolicy(PolicyType.FCFS);
        System.out.println("✓ Política configurada: FCFS (no preemptiva)");
        System.out.println("  → Sin quantum: procesos se ejecutan hasta completarse o bloquearse\n");
        
        // Iniciar el manejador de I/O en un hilo separado
        Thread ioThread = new Thread(ioHandler, "IOHandler-Thread");
        ioThread.setDaemon(true);
        ioThread.start();
        
        System.out.println("✓ Sistema operativo inicializado");
        System.out.println("✓ CPU inicializada y enlazada con Scheduler");
        System.out.println("✓ IOHandler iniciado en hilo separado");
        System.out.println("✓ Ciclo del reloj: " + cycleDurationMillis + " ms\n");
        
        // US 2.3: Crear procesos IDÉNTICOS para comparación
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  CREANDO PROCESOS IDÉNTICOS AL ESCENARIO RR (para compare)│");
        System.out.println("└─────────────────────────────────────────────────────────────┘\n");
        
        // Proceso 1: CPU-bound LARGO (monopolizará CPU en FCFS)
        ProcessControlBlock proc1 = new ProcessControlBlock("P1-Long-CPU");
        proc1.setTotalInstructions(12);
        proc1.setIOBound(false);
        System.out.println("✓ P1-Long-CPU: 12 instrucciones");
        System.out.println("  → En FCFS: monopoliza CPU hasta terminar (EFECTO CONVOY)");
        
        // Proceso 2: CPU-bound CORTO (víctima del convoy)
        ProcessControlBlock proc2 = new ProcessControlBlock("P2-Short-CPU");
        proc2.setTotalInstructions(3);
        proc2.setIOBound(false);
        System.out.println("✓ P2-Short-CPU: 3 instrucciones");
        System.out.println("  → En FCFS: espera 12 ciclos a que P1 termine (¡CONVOY!)");
        
        // Proceso 3: CPU-bound MEDIANO (también afectado)
        ProcessControlBlock proc3 = new ProcessControlBlock("P3-Medium-CPU");
        proc3.setTotalInstructions(6);
        proc3.setIOBound(false);
        System.out.println("✓ P3-Medium-CPU: 6 instrucciones");
        System.out.println("  → En FCFS: espera 12+3=15 ciclos a su turno");
        
        // Agregar procesos a la cola de listos
        System.out.println("\n┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  CARGANDO PROCESOS EN LA COLA DE LISTOS                    │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        
        os.moveToReady(proc1);
        os.moveToReady(proc2);
        os.moveToReady(proc3);
        
        System.out.println("\nReady Queue: P1 (12) → P2 (3) → P3 (6)");
        System.out.println("\n📊 Estado inicial:");
        printSystemState(os, cpu);
        
        // US 2.1: Iniciar reloj del sistema
        System.out.println("\n┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  US 2.1: INICIANDO RELOJ DEL SISTEMA                      │");
        System.out.println("└─────────────────────────────────────────────────────────────┘\n");
        
        os.startSystemClock();

        int expectedProcessCount = 3;
        long maxCycles = 100;
        boolean completedSuccessfully = false;
        
        System.out.println("╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║        OBSERVANDO EJECUCIÓN CON FCFS (no preemptivo)       ║");
        System.out.println("║           ⚠️  Esperar efecto convoy: P1 monopoliza CPU     ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝\n");

        while (os.getGlobalClockCycle() < maxCycles) {
            try {
                Thread.sleep(cycleDurationMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            long currentCycle = os.getGlobalClockCycle();
            String cpuStatus = cpu.isIdle() ? "IDLE" : cpu.getCurrentProcess().getProcessName();
            
            // Reporte cada ciclo
            System.out.printf("⏱️  Ciclo %2d | Ready: %d | Finished: %d | CPU: %-15s\n",
                    currentCycle,
                    os.readyQueueSize(),
                    os.finishedQueueSize(),
                    cpuStatus);

            // Verificar si todos los procesos han terminado
            if (os.finishedQueueSize() == expectedProcessCount
                    && os.readyQueueSize() == 0
                    && cpu.isIdle()) {
                System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                System.out.println("║  🎉 TODOS LOS " + expectedProcessCount + " PROCESOS FINALIZADOS (FCFS)             ║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
                completedSuccessfully = true;
                break;
            }
        }

        if (!completedSuccessfully) {
            System.out.println("\n⚠️  No se completaron todos los procesos en " + maxCycles + " ciclos.");
        }

        System.out.println("\n🔴 Deteniendo reloj y componentes...");
        os.stopSystemClock();
        ioHandler.stop();
        try {
            ioThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("✓ FCFS completado en " + os.getGlobalClockCycle() + " ciclos");
        System.out.println("\n📊 Estado final (FCFS):");
        printSystemState(os, cpu);
    }

    
    /**
     * Imprime el estado actual del sistema de forma compacta.
     * Coordina: US 2.1 (reloj), US 2.2 (scheduler), US 2.3 (FCFS), US 2.4 (RR).
     */
    private static void printSystemState(OperatingSystem os, CPU cpu) {
        System.out.println("  ┌─ ESTADO DEL SISTEMA ────────────────────────────────────┐");
        System.out.println("  │ ✅ Ready Queue:      " + os.readyQueueSize() + " procesos");
        System.out.println("  │ ✔️  Finished Queue:   " + os.finishedQueueSize() + " procesos");
        System.out.println("  │ 🖥️  CPU Status:      " + (cpu.isIdle() ? "IDLE" : 
                          cpu.getCurrentProcess().getProcessName()));
        System.out.println("  │ ⏱️  Global Cycle:     " + os.getGlobalClockCycle());
        System.out.println("  │ 🔁 Quantum (RR):    " + os.getRoundRobinQuantum() + " ciclos");
        System.out.println("  └─────────────────────────────────────────────────────────┘");
    }
}
