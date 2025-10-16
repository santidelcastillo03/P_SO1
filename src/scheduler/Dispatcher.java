/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduler;

import core.CPU;
import core.ProcessControlBlock;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Dispatcher coordina la carga de procesos seleccionados por el planificador en la CPU disponible.
 */
public class Dispatcher {

    /** Logger para diagnósticos de despacho en el simulador. */
    private static final Logger LOGGER = Logger.getLogger(Dispatcher.class.getName());

    /**
     * Carga el proceso indicado en la CPU si esta se encuentra libre.
     * @param pcb proceso a despachar (puede ser null)
     * @param cpu unidad de cómputo que recibirá al proceso
     */
    public void dispatch(ProcessControlBlock pcb, CPU cpu) {
        Objects.requireNonNull(cpu, "La CPU de destino no puede ser nula");
        if (pcb == null) {
            return;
        }
        if (!cpu.isIdle()) {
            LOGGER.fine(() -> String.format("CPU ocupada, se omite despacho de %s (#%d)",
                    pcb.getProcessName(),
                    pcb.getProcessId()));
            return;
        }
        cpu.loadProcess(pcb);
        LOGGER.fine(() -> String.format("Despachador cargó %s (#%d) en CPU",
                pcb.getProcessName(),
                pcb.getProcessId()));
    }
}
