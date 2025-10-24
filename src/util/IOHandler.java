/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package util;

import core.OperatingSystem;
import core.ProcessControlBlock;
import datastructures.CustomQueue;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit; //enum that provides a set of time units
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * IOHandler mantiene una cola de procesos esperando la finalización de eventos de I/O y los procesa de forma asíncrona.
 * Permite encolar procesos bloqueados por entrada/salida, simular el retardo de I/O y reinsertarlos en la cola de listos del sistema operativo.
 */
public class IOHandler implements Runnable {

    /* Logger para mensajes de depuración y seguimiento de eventos de I/O. */
    private static final Logger LOGGER = Logger.getLogger(IOHandler.class.getName());

    /* Referencia al sistema operativo para reinsertar procesos en la cola de listos. */
    private final OperatingSystem operatingSystem;
    /* Cola de procesos esperando la finalización de I/O. */
    private final CustomQueue<ProcessControlBlock> ioQueue;
    /* Semáforo que indica la cantidad de procesos pendientes de I/O. */
    private final Semaphore pendingProcesses;
    /* Indica si el manejador debe seguir ejecutándose. */
    private volatile boolean running;
    /* Duración de un ciclo de CPU en milisegundos (para simular retardo de I/O). */
    private volatile long cycleDurationMillis;

    /*
     * Construye una instancia de IOHandler con una cola vacía para procesos bloqueados.
     * @param operatingSystem sistema operativo usado para reinsertar procesos en la cola de listos
     * @param cycleDurationMillis duración de un ciclo de CPU en milisegundos
     */
    public IOHandler(OperatingSystem operatingSystem, long cycleDurationMillis) {
        this.operatingSystem = Objects.requireNonNull(operatingSystem, "El sistema operativo no puede ser nulo");
        this.ioQueue = new CustomQueue<>();
        this.pendingProcesses = new Semaphore(0);
        this.running = true;
        this.cycleDurationMillis = Math.max(0L, cycleDurationMillis);
    }

    /*
     * Encola un proceso para que el subsistema de I/O lo reanude cuando termine la operación.
     * @param pcb proceso que espera la finalización de I/O
     */
    public void enqueueProcess(ProcessControlBlock pcb) {
        Objects.requireNonNull(pcb, "El proceso en I/O no puede ser nulo");
        ioQueue.enqueue(pcb);
        pendingProcesses.release();
        LOGGER.info(() -> String.format("Proceso %s (#%d) encolado para I/O",
                pcb.getProcessName(),
                pcb.getProcessId()));
    }

    /*
     * Recupera el siguiente proceso que espera la finalización de I/O.
     * @return siguiente proceso bloqueado o null si no hay procesos esperando
     */
    public ProcessControlBlock dequeueProcess() {
        return ioQueue.dequeue();
    }

    /*
     * Da visibilidad sobre la cantidad de procesos encolados esperando I/O.
     * @return cantidad de procesos en espera de finalización de I/O
     */
    public int queuedProcessCount() {
        return ioQueue.size();
    }

    /*
     * Actualiza la duración de un ciclo de CPU utilizada para calcular los retardos simulados de I/O.
     * @param cycleDurationMillis nueva duración en milisegundos
     */
    public void setCycleDurationMillis(long cycleDurationMillis) {
        this.cycleDurationMillis = Math.max(0L, cycleDurationMillis);
    }

    /*
     * Detiene el bucle del manejador de forma segura.
     */
    public void stop() {
        running = false;
        pendingProcesses.release();
    }

    /*
     * Ejecuta el bucle asíncrono que procesa las finalizaciones de I/O.
     * Extrae procesos de la cola, simula el retardo de I/O y los reinserta en la cola de listos.
     */
    @Override
    public void run() {
        while (running || queuedProcessCount() > 0) {
            try {
                // Intentar adquirir un permiso del semáforo con timeout
                if (!pendingProcesses.tryAcquire(1, java.util.concurrent.TimeUnit.SECONDS)) {
                    // Si el timeout se agota y no está corriendo, salir del bucle
                    if (!running && queuedProcessCount() == 0) {
                        break;
                    }
                    continue;
                }
                
                if (!running && queuedProcessCount() == 0) {
                    break;
                }
                
                ProcessControlBlock pcb = dequeueProcess();
                if (pcb == null) {
                    continue;
                }
                
                long sleepTime = Math.max(0L, pcb.getIoDuration() * cycleDurationMillis);
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
                
                operatingSystem.completeIo(pcb);
                LOGGER.info(() -> String.format("Proceso %s (#%d) completó I/O y retorna a readyQueue",
                        pcb.getProcessName(),
                        pcb.getProcessId()));
            } catch (InterruptedException ex) {
                // La interrupción es normal durante el cierre
                LOGGER.log(Level.FINE, "Hilo de IOHandler interrumpido durante shutdown", ex);
                // Restaurar el estado de interrupción
                Thread.currentThread().interrupt();
                // Si estamos deteniendo, salir de forma controlada
                if (!running) {
                    break;
                }
            }
        }
        LOGGER.info("IOHandler detenido");
    }
}
