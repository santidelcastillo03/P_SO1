/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package p_so1;

import core.CPU;
import core.OperatingSystem;
import core.ProcessControlBlock;
import util.IOHandler;

/**
 * Programa principal para probar el simulador de SO con gestión de suspensión (US 1.7).
 * Demuestra el swapping: cuando la memoria está llena (MAX=4), procesos adicionales 
 * se suspenden (LISTO_SUSPENDIDO) y se restauran cuando hay espacio disponible.
 * 
 * @author Santiago
 */
public class P_so1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║   SIMULADOR DE SO - PRUEBA DE GESTIÓN DE SUSPENSIÓN (US 1.7)  ║");
        System.out.println("║              MAX_PROCESSES_IN_MEMORY = 4                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        // Crear componentes del sistema
        OperatingSystem os = new OperatingSystem();
        IOHandler ioHandler = new IOHandler(os, 100); // 100ms por ciclo de I/O
        CPU cpu = new CPU(os, ioHandler);
        
        // Iniciar el manejador de I/O en un hilo separado
        Thread ioThread = new Thread(ioHandler, "IOHandler-Thread");
        ioThread.setDaemon(true);
        ioThread.start();
        
        System.out.println("✓ Sistema operativo inicializado (Límite de memoria: 4 procesos)");
        System.out.println("✓ CPU inicializada");
        System.out.println("✓ IOHandler iniciado en hilo separado\n");
        
        // Crear procesos de prueba - CREAMOS 6 PARA FORZAR SUSPENSIÓN
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│     CREANDO 6 PROCESOS (excede límite de memoria = 4)   │");
        System.out.println("└──────────────────────────────────────────────────────────┘\n");
        
        // Proceso 1: CPU-bound corto
        ProcessControlBlock proc1 = new ProcessControlBlock("P1-Short");
        proc1.setTotalInstructions(5);
        proc1.setIOBound(false);
        System.out.println("✓ " + proc1.getProcessName() + " creado (CPU-bound, 5 instrucciones)");
        
        // Proceso 2: CPU-bound mediano
        ProcessControlBlock proc2 = new ProcessControlBlock("P2-Medium");
        proc2.setTotalInstructions(8);
        proc2.setIOBound(false);
        System.out.println("✓ " + proc2.getProcessName() + " creado (CPU-bound, 8 instrucciones)");
        
        // Proceso 3: I/O-bound
        ProcessControlBlock proc3 = new ProcessControlBlock("P3-IO");
        proc3.setTotalInstructions(12);
        proc3.setIOBound(true);
        proc3.setIoExceptionCycle(4); // I/O en ciclo 4
        proc3.setIoDuration(2); // 2 ciclos de duración
        System.out.println("✓ " + proc3.getProcessName() + " creado (I/O-bound, interrumpe en ciclo 4, dura 2 ciclos)");
        
        // Proceso 4: CPU-bound largo
        ProcessControlBlock proc4 = new ProcessControlBlock("P4-Long");
        proc4.setTotalInstructions(10);
        proc4.setIOBound(false);
        System.out.println("✓ " + proc4.getProcessName() + " creado (CPU-bound, 10 instrucciones)");
        
        // Proceso 5: CPU-bound (será suspendido)
        ProcessControlBlock proc5 = new ProcessControlBlock("P5-ToSuspend");
        proc5.setTotalInstructions(6);
        proc5.setIOBound(false);
        System.out.println("✓ " + proc5.getProcessName() + " creado (CPU-bound, 6 instrucciones) ⚠️ CANDIDATO A SUSPENSIÓN");
        
        // Proceso 6: CPU-bound (será suspendido)
        ProcessControlBlock proc6 = new ProcessControlBlock("P6-ToSuspend");
        proc6.setTotalInstructions(7);
        proc6.setIOBound(false);
        System.out.println("✓ " + proc6.getProcessName() + " creado (CPU-bound, 7 instrucciones) ⚠️ CANDIDATO A SUSPENSIÓN");
        
        // Agregar procesos a la cola de listos
        System.out.println("\n┌──────────────────────────────────────────────────────────┐");
        System.out.println("│  CARGANDO 6 PROCESOS (Límite: 4 en memoria)             │");
        System.out.println("│  Esperamos que P5 y P6 sean SUSPENDIDOS automáticamente │");
        System.out.println("└──────────────────────────────────────────────────────────┘");
        
        System.out.println("\n[1/6] Moviendo P1 a readyQueue...");
        os.moveToReady(proc1);
        printSystemState(os, cpu);
        
        System.out.println("\n[2/6] Moviendo P2 a readyQueue...");
        os.moveToReady(proc2);
        printSystemState(os, cpu);
        
        System.out.println("\n[3/6] Moviendo P3 a readyQueue...");
        os.moveToReady(proc3);
        printSystemState(os, cpu);
        
        System.out.println("\n[4/6] Moviendo P4 a readyQueue...");
        os.moveToReady(proc4);
        printSystemState(os, cpu);
        
        System.out.println("\n⚠️ [5/6] Moviendo P5 a readyQueue (MEMORIA LLENA - SUSPENDERÁ P1)...");
        os.moveToReady(proc5);
        printSystemState(os, cpu);
        
        System.out.println("\n⚠️ [6/6] Moviendo P6 a readyQueue (MEMORIA LLENA - SUSPENDERÁ P2)...");
        os.moveToReady(proc6);
        printSystemState(os, cpu);
        
        System.out.println("\n📊 Estado inicial después de cargar 6 procesos:");
        printSystemState(os, cpu);
        
        // Simular ejecución de procesos (simple FCFS manual)
        System.out.println("\n┌════════════════════════════════════════════════════════════════┐");
        System.out.println("│          INICIANDO SIMULACIÓN CON SWAPPING (FCFS)             │");
        System.out.println("│  A medida que procesos terminen, los suspendidos se restauran │");
        System.out.println("└════════════════════════════════════════════════════════════════┘\n");
        
        int globalClock = 0;
        int maxCycles = 100; // Aumentado para dar tiempo a 6 procesos
        
        while (globalClock < maxCycles) {
            globalClock++;
            System.out.println("\n⏰ ════ CICLO " + globalClock + " ════════════════════════════════════");
            
            // Si la CPU está ociosa, cargar siguiente proceso (simple FCFS)
            if (cpu.isIdle() && os.readyQueueSize() > 0) {
                ProcessControlBlock nextProcess = os.dequeueReady();
                if (nextProcess != null) {
                    cpu.loadProcess(nextProcess);
                    System.out.println("  🔄 CPU carga proceso: " + nextProcess.getProcessName() + 
                                     " [#" + nextProcess.getProcessId() + "]");
                }
            }
            
            // Ejecutar ciclo de CPU
            if (!cpu.isIdle()) {
                ProcessControlBlock current = cpu.getCurrentProcess();
                int pcBefore = current.getProgramCounter();
                System.out.println("  ⚙️  CPU ejecuta: " + current.getProcessName() + 
                                 " | PC=" + pcBefore + "/" + current.getTotalInstructions() +
                                 " | MAR=" + current.getMemoryAddressRegister());
                
                cpu.executeCycle();
                
                // Verificar si terminó
                if (current.getProgramCounter() >= current.getTotalInstructions()) {
                    System.out.println("  ✅ Proceso " + current.getProcessName() + " TERMINADO");
                    os.markAsFinished(current);
                    cpu.releaseProcess(); // Liberar CPU
                }
            } else {
                System.out.println("  💤 CPU IDLE (esperando procesos)");
            }
            
            // Mostrar estado
            System.out.println("  📊 Estado → Ready: " + os.readyQueueSize() + 
                             " | Blocked: " + os.blockedQueueSize() + 
                             " | Finished: " + os.finishedQueueSize());
            
            // Condición de salida - ahora son 6 procesos
            if (os.finishedQueueSize() == 6 && os.readyQueueSize() == 0 && 
                os.blockedQueueSize() == 0 && cpu.isIdle()) {
                System.out.println("\n🎉 ══════════════════════════════════════════════════════════");
                System.out.println("    TODOS LOS 6 PROCESOS FINALIZADOS EXITOSAMENTE");
                System.out.println("    (incluyendo los que fueron suspendidos y restaurados)");
                System.out.println("   ══════════════════════════════════════════════════════════");
                break;
            }
            
            // Pequeña pausa para ver el output
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║           SIMULACIÓN FINALIZADA                    ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println("Total de ciclos ejecutados: " + globalClock);
        System.out.println("\n📊 Estado final del sistema:");
        printSystemState(os, cpu);
        
        // Detener IOHandler
        System.out.println("\n🔴 Deteniendo IOHandler...");
        ioHandler.stop();
        try {
            ioThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("✓ Sistema detenido correctamente");
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              PRUEBA COMPLETADA ✓                   ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }
    
    /**
     * Imprime el estado actual del sistema incluyendo colas de suspendidos.
     */
    private static void printSystemState(OperatingSystem os, CPU cpu) {
        System.out.println("  ┌─ ESTADO DEL SISTEMA ────────────────────────────────┐");
        System.out.println("  │ 💾 Procesos en Memoria: " + os.getProcessesInMemory() + " / 4 (límite)");
        System.out.println("  │ ✅ Ready Queue:         " + os.readyQueueSize() + " procesos");
        System.out.println("  │ 🔒 Blocked Queue:       " + os.blockedQueueSize() + " procesos");
        System.out.println("  │ ⏸️  Ready Suspended:     " + os.readySuspendedQueueSize() + " procesos (swapped out)");
        System.out.println("  │ ⏸️  Blocked Suspended:   " + os.blockedSuspendedQueueSize() + " procesos (swapped out)");
        System.out.println("  │ ✔️  Finished:            " + os.finishedQueueSize() + " procesos");
        System.out.println("  │ 🖥️  CPU: " + (cpu.isIdle() ? "IDLE" : 
                          "Ejecutando " + cpu.getCurrentProcess().getProcessName()));
        System.out.println("  └──────────────────────────────────────────────────────┘");
    }
}
