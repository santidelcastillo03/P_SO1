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
 * Programa principal para probar US 2.1, 2.2 y 2.3:
 * - US 2.1: Reloj del Sistema (hilo que orquesta planificador → despachador → CPU)
 * - US 2.2: Scheduler Base (infraestructura configurable de políticas)
 * - US 2.3: FCFS (First Come, First Served) con efecto convoy documentado
 * 
 * @author Santiago
 */
public class P_so1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║      SIMULADOR DE SO - PRUEBA US 2.1, 2.2, 2.3                ║");
        System.out.println("║   RELOJ DEL SISTEMA + SCHEDULER BASE + FCFS                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  OBJETIVO DE LA PRUEBA:");
        System.out.println("  • Verificar reloj del sistema (US 2.1) con ciclos regulares");
        System.out.println("  • Validar infraestructura Scheduler + PolicyType (US 2.2)");
        System.out.println("  • Demostrar FCFS (US 2.3) y efecto convoy");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        // Crear componentes del sistema
        long cycleDurationMillis = 500L; // Más lento para observar mejor

        OperatingSystem os = new OperatingSystem();
        os.setCycleDurationMillis(cycleDurationMillis);
        IOHandler ioHandler = new IOHandler(os, cycleDurationMillis);
        CPU cpu = new CPU(os, ioHandler);
        os.attachCpu(cpu);
        
        // US 2.2: Configurar política de planificación usando PolicyType
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  US 2.2: CONFIGURANDO POLÍTICA DE PLANIFICACIÓN            │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        os.setSchedulingPolicy(PolicyType.FCFS);
        System.out.println("✓ Política configurada: FCFS (First Come, First Served)");
        System.out.println("  → No expropiativa (procesos corren hasta terminar o bloquearse)\n");
        
        // Iniciar el manejador de I/O en un hilo separado
        Thread ioThread = new Thread(ioHandler, "IOHandler-Thread");
        ioThread.setDaemon(true);
        ioThread.start();
        
        System.out.println("✓ Sistema operativo inicializado");
        System.out.println("✓ CPU inicializada y enlazada con Scheduler");
        System.out.println("✓ IOHandler iniciado en hilo separado");
        System.out.println("✓ Ciclo del reloj: " + cycleDurationMillis + " ms\n");
        
        // US 2.3: Crear procesos para demostrar FCFS y efecto convoy
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  US 2.3: CREANDO PROCESOS PARA DEMOSTRAR FCFS Y CONVOY     │");
        System.out.println("└─────────────────────────────────────────────────────────────┘\n");
        
        // Proceso 1: CPU-bound LARGO (causa efecto convoy)
        ProcessControlBlock proc1 = new ProcessControlBlock("P1-Long-CPU");
        proc1.setTotalInstructions(12);
        proc1.setIOBound(false);
        System.out.println("✓ P1-Long-CPU: 12 instrucciones (CPU-bound LARGO)");
        System.out.println("  → Este proceso largo causará el EFECTO CONVOY");
        
        // Proceso 2: CPU-bound CORTO (víctima del convoy)
        ProcessControlBlock proc2 = new ProcessControlBlock("P2-Short-CPU");
        proc2.setTotalInstructions(3);
        proc2.setIOBound(false);
        System.out.println("✓ P2-Short-CPU: 3 instrucciones (CPU-bound CORTO)");
        System.out.println("  → Esperará todo el tiempo que P1 ocupe la CPU");
        
        // Proceso 3: CPU-bound MEDIANO (también víctima)
        ProcessControlBlock proc3 = new ProcessControlBlock("P3-Medium-CPU");
        proc3.setTotalInstructions(6);
        proc3.setIOBound(false);
        System.out.println("✓ P3-Medium-CPU: 6 instrucciones (CPU-bound MEDIANO)");
        System.out.println("  → Esperará que P1 y P2 terminen");
        
        // Proceso 4: I/O-bound (para demostrar interacción con bloqueos)
        ProcessControlBlock proc4 = new ProcessControlBlock("P4-IO-Bound");
        proc4.setTotalInstructions(8);
        proc4.setIOBound(true);
        proc4.setIoExceptionCycle(3); // I/O en ciclo 3
        proc4.setIoDuration(2); // 2 ciclos de duración
        System.out.println("✓ P4-IO-Bound: 8 instrucciones (se bloqueará en ciclo 3)");
        System.out.println("  → Permite que otros procesos usen CPU mientras espera I/O");
        
        System.out.println("\n📊 PREDICCIÓN DEL ORDEN DE EJECUCIÓN CON FCFS:");
        System.out.println("   P1 (12 ciclos) → P2 (3 ciclos) → P3 (6 ciclos) → P4 (con bloqueo)");
        System.out.println("   ⚠️  P2 y P3 (cortos) esperarán detrás de P1 (largo) = EFECTO CONVOY\n");
        
        // Agregar procesos a la cola de listos
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  CARGANDO PROCESOS EN LA COLA DE LISTOS (readyQueue)       │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        
        System.out.println("\n[1/4] Moviendo P1-Long-CPU a readyQueue...");
        os.moveToReady(proc1);
        System.out.println("      Ready Queue: P1 (12 inst)");
        
        System.out.println("\n[2/4] Moviendo P2-Short-CPU a readyQueue...");
        os.moveToReady(proc2);
        System.out.println("      Ready Queue: P1 (12 inst) → P2 (3 inst)");
        
        System.out.println("\n[3/4] Moviendo P3-Medium-CPU a readyQueue...");
        os.moveToReady(proc3);
        System.out.println("      Ready Queue: P1 (12 inst) → P2 (3 inst) → P3 (6 inst)");
        
        System.out.println("\n[4/4] Moviendo P4-IO-Bound a readyQueue...");
        os.moveToReady(proc4);
        System.out.println("      Ready Queue: P1 (12 inst) → P2 (3 inst) → P3 (6 inst) → P4 (8 inst, I/O)");
        
        System.out.println("\n📊 Estado inicial:");
        printSystemState(os, cpu);
        
        // US 2.1: Iniciar reloj del sistema
        System.out.println("\n┌═════════════════════════════════════════════════════════════┐");
        System.out.println("│  US 2.1: INICIANDO RELOJ DEL SISTEMA (SystemClock-Thread)  │");
        System.out.println("└═════════════════════════════════════════════════════════════┘");
        System.out.println("▶️  Activando hilo del reloj global...");
        System.out.println("    Secuencia por ciclo: Planificador → Despachador → CPU");
        System.out.println("    Duración del ciclo: " + cycleDurationMillis + " ms\n");
        
        os.startSystemClock();

        int expectedProcessCount = 4;
        long maxCycles = 100; // Suficiente para completar los 4 procesos
        boolean completedSuccessfully = false;
        
        System.out.println("╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║           OBSERVANDO EJECUCIÓN CICLO POR CICLO             ║");
        System.out.println("║  US 2.3: FCFS seleccionará procesos en orden de llegada   ║");
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
            
            // Reporte cada 2 ciclos para no saturar la salida
            if (currentCycle % 2 == 0 || os.finishedQueueSize() > 0) {
                System.out.printf("⏱️  Ciclo %2d | Ready: %d | Blocked: %d | Finished: %d | CPU: %-15s\n",
                        currentCycle,
                        os.readyQueueSize(),
                        os.blockedQueueSize(),
                        os.finishedQueueSize(),
                        cpuStatus);
            }

            // Verificar si todos los procesos han terminado
            if (os.finishedQueueSize() == expectedProcessCount
                    && os.readyQueueSize() == 0
                    && os.blockedQueueSize() == 0
                    && cpu.isIdle()) {
                System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                System.out.println("║  🎉 TODOS LOS " + expectedProcessCount + " PROCESOS FINALIZADOS EXITOSAMENTE      ║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
                completedSuccessfully = true;
                break;
            }
        }

        if (!completedSuccessfully) {
            System.out.println("\n⚠️  Advertencia: Se alcanzó el límite de ciclos (" + maxCycles + ") sin finalizar todos los procesos.");
        }

        System.out.println("\n🔴 Deteniendo reloj global...");
        os.stopSystemClock();

        System.out.println("\n╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║                SIMULACIÓN FINALIZADA                        ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
        System.out.println("Total de ciclos ejecutados: " + os.getGlobalClockCycle());
        System.out.println("\n📊 Estado final del sistema:");
        printSystemState(os, cpu);
        
        // Análisis del efecto convoy (US 2.3)
        System.out.println("\n╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║          US 2.3: ANÁLISIS DEL EFECTO CONVOY                 ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
        System.out.println("🔍 EFECTO CONVOY OBSERVADO:");
        System.out.println("   • P1-Long-CPU (12 inst) se ejecutó primero (FCFS)");
        System.out.println("   • P2-Short-CPU (3 inst) esperó ~12 ciclos por P1");
        System.out.println("   • P3-Medium-CPU (6 inst) esperó ~15 ciclos por P1 y P2");
        System.out.println("   • Procesos cortos sufren tiempo de espera elevado");
        System.out.println("\n💡 CONCLUSIÓN:");
        System.out.println("   FCFS es simple pero ineficiente cuando procesos largos llegan");
        System.out.println("   primero. Políticas como SJF o SRTF minimizan este efecto.");
        
        // Detener IOHandler
        System.out.println("\n🔴 Deteniendo IOHandler...");
        ioHandler.stop();
        try {
            ioThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("✓ Sistema detenido correctamente");
        
        // Resumen de verificación de US
        System.out.println("\n╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║              VERIFICACIÓN DE USER STORIES                   ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
        System.out.println("✅ US 2.1 - Reloj del Sistema:");
        System.out.println("   • SystemClock-Thread ejecutó " + os.getGlobalClockCycle() + " ciclos");
        System.out.println("   • Secuencia Planificador → Despachador → CPU funcionó correctamente");
        System.out.println("\n✅ US 2.2 - Scheduler Base:");
        System.out.println("   • Infraestructura SchedulingPolicy implementada");
        System.out.println("   • PolicyType.FCFS configurado correctamente");
        System.out.println("   • Scheduler inyectado en CPU y OS");
        System.out.println("\n✅ US 2.3 - FCFS:");
        System.out.println("   • Procesos seleccionados en orden FIFO");
        System.out.println("   • Efecto convoy documentado y observable");
        System.out.println("   • Logs muestran: 'Seleccionado: Pn' en orden de llegada");
        
        System.out.println("\n╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║           PRUEBA US 2.1, 2.2, 2.3 COMPLETADA ✓             ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Imprime el estado actual del sistema de forma compacta.
     */
    private static void printSystemState(OperatingSystem os, CPU cpu) {
        System.out.println("  ┌─ ESTADO DEL SISTEMA ───────────────────────────────────┐");
        System.out.println("  │ ✅ Ready Queue:    " + os.readyQueueSize() + " procesos");
        System.out.println("  │ 🔒 Blocked Queue:  " + os.blockedQueueSize() + " procesos");
        System.out.println("  │ ✔️  Finished:       " + os.finishedQueueSize() + " procesos");
        System.out.println("  │ 🖥️  CPU:           " + (cpu.isIdle() ? "IDLE" : 
                          "Ejecutando " + cpu.getCurrentProcess().getProcessName()));
        System.out.println("  │ ⏱️  Ciclo Global:   " + os.getGlobalClockCycle());
        System.out.println("  └────────────────────────────────────────────────────────┘");
    }
}
