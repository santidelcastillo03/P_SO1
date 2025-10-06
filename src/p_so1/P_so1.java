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
 * Programa principal para probar el simulador de SO.
 * Crea procesos de prueba (CPU-bound e I/O-bound) y simula su ejecución.
 * @author Santiago
 */
public class P_so1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║   SIMULADOR DE SISTEMA OPERATIVO - PRUEBA BÁSICA   ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        // Crear componentes del sistema
        OperatingSystem os = new OperatingSystem();
        IOHandler ioHandler = new IOHandler(os, 100); // 100ms por ciclo de I/O
        CPU cpu = new CPU(os, ioHandler);
        
        // Iniciar el manejador de I/O en un hilo separado
        Thread ioThread = new Thread(ioHandler, "IOHandler-Thread");
        ioThread.setDaemon(true);
        ioThread.start();
        
        System.out.println("✓ Sistema operativo inicializado");
        System.out.println("✓ CPU inicializada");
        System.out.println("✓ IOHandler iniciado en hilo separado\n");
        
        // Crear procesos de prueba
        System.out.println("┌─────────────────────────────────┐");
        System.out.println("│     CREANDO PROCESOS DE PRUEBA  │");
        System.out.println("└─────────────────────────────────┘\n");
        
        // Proceso 1: CPU-bound (sin I/O)
    ProcessControlBlock proc1 = new ProcessControlBlock("P1-CPU");
    proc1.setTotalInstructions(10);
    proc1.setIOBound(false);
    System.out.println("✓ " + proc1.getProcessName() + " creado (CPU-bound, 10 instrucciones)");
        
    // Proceso 2: I/O-bound
    ProcessControlBlock proc2 = new ProcessControlBlock("P2-IO");
    proc2.setTotalInstructions(15);
    proc2.setIOBound(true);
    proc2.setIoExceptionCycle(5); // I/O en ciclo 5
    proc2.setIoDuration(3); // 3 ciclos de duración
    System.out.println("✓ " + proc2.getProcessName() + " creado (I/O-bound, interrumpe en ciclo 5, dura 3 ciclos)");
        
    // Proceso 3: CPU-bound simple
    ProcessControlBlock proc3 = new ProcessControlBlock("P3-CPU");
    proc3.setTotalInstructions(8);
    proc3.setIOBound(false);
    System.out.println("✓ " + proc3.getProcessName() + " creado (CPU-bound, 8 instrucciones)");
        
        // Agregar procesos a la cola de listos
        System.out.println("\n┌─────────────────────────────────┐");
        System.out.println("│  CARGANDO A COLA DE LISTOS      │");
        System.out.println("└─────────────────────────────────┘");
        os.moveToReady(proc1);
        os.moveToReady(proc2);
        os.moveToReady(proc3);
        
        System.out.println("\n📊 Estado inicial:");
        printSystemState(os, cpu);
        
        // Simular ejecución de procesos (simple FCFS manual)
        System.out.println("\n┌═════════════════════════════════════════════════════┐");
        System.out.println("│          INICIANDO SIMULACIÓN (FCFS SIMPLE)         │");
        System.out.println("└═════════════════════════════════════════════════════┘\n");
        
        int globalClock = 0;
        int maxCycles = 50; // Límite de seguridad
        
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
            
            // Condición de salida
            if (os.finishedQueueSize() == 3 && os.readyQueueSize() == 0 && 
                os.blockedQueueSize() == 0 && cpu.isIdle()) {
                System.out.println("\n🎉 ═══════════════════════════════════════════════════");
                System.out.println("    TODOS LOS PROCESOS FINALIZADOS EXITOSAMENTE");
                System.out.println("   ═══════════════════════════════════════════════════");
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
     * Imprime el estado actual del sistema.
     */
    private static void printSystemState(OperatingSystem os, CPU cpu) {
        System.out.println("  • Ready Queue: " + os.readyQueueSize() + " procesos");
        System.out.println("  • Blocked Queue: " + os.blockedQueueSize() + " procesos");
        System.out.println("  • Finished: " + os.finishedQueueSize() + " procesos");
        System.out.println("  • CPU: " + (cpu.isIdle() ? "IDLE" : 
                          "Ejecutando " + cpu.getCurrentProcess().getProcessName()));
    }
}
