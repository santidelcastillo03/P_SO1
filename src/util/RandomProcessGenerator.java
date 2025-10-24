/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import core.ProcessControlBlock;
import java.util.Random;

/**
 * Generador de procesos aleatorios para pruebas y simulaciones.
 *
 * @author santiagodelcastillo
 */
public class RandomProcessGenerator {

    private final Random random;
    private int processCounter;

    private static final int MIN_INSTRUCTIONS = 5;
    private static final int MAX_INSTRUCTIONS = 50;
    private static final int MIN_IO_CYCLE = 3;
    private static final int MAX_IO_CYCLE = 20;
    private static final int MIN_IO_DURATION = 2;
    private static final int MAX_IO_DURATION = 10;
    private static final double IO_BOUND_PROBABILITY = 0.4;

    /**
     * Constructor que inicializa el generador con una semilla aleatoria.
     */
    public RandomProcessGenerator() {
        this.random = new Random();
        this.processCounter = 1;
    }

    /**
     * Constructor que permite especificar una semilla para reproducibilidad.
     * @param seed semilla para el generador aleatorio
     */
    public RandomProcessGenerator(long seed) {
        this.random = new Random(seed);
        this.processCounter = 1;
    }

    /**
     * Genera un proceso con características aleatorias.
     * @return ProcessControlBlock con parámetros aleatorios
     */
    public ProcessControlBlock generateRandomProcess() {
        String name = "P" + processCounter++;
        ProcessControlBlock pcb = new ProcessControlBlock(name);

        // Generar número aleatorio de instrucciones
        int instructions = MIN_INSTRUCTIONS + random.nextInt(MAX_INSTRUCTIONS - MIN_INSTRUCTIONS + 1);
        pcb.setTotalInstructions(instructions);

        // Determinar si es I/O bound
        boolean isIOBound = random.nextDouble() < IO_BOUND_PROBABILITY;
        pcb.setIOBound(isIOBound);

        if (isIOBound) {
            // Generar ciclo de I/O aleatorio (entre MIN_IO_CYCLE y el total de instrucciones)
            int maxIoCycle = Math.min(instructions - 1, MAX_IO_CYCLE);
            int ioCycle = MIN_IO_CYCLE + random.nextInt(Math.max(1, maxIoCycle - MIN_IO_CYCLE + 1));
            pcb.setIoExceptionCycle(ioCycle);

            // Generar duración de I/O aleatoria
            int ioDuration = MIN_IO_DURATION + random.nextInt(MAX_IO_DURATION - MIN_IO_DURATION + 1);
            pcb.setIoDuration(ioDuration);
        } else {
            pcb.setIoExceptionCycle(-1);
            pcb.setIoDuration(0);
        }

        return pcb;
    }

    /**
     * Genera un ciclo de arribo aleatorio dentro de un rango.
     * @param minCycle ciclo mínimo de arribo
     * @param maxCycle ciclo máximo de arribo
     * @return ciclo de arribo aleatorio
     */
    public int generateRandomArrivalCycle(int minCycle, int maxCycle) {
        if (minCycle >= maxCycle) {
            return minCycle;
        }
        return minCycle + random.nextInt(maxCycle - minCycle + 1);
    }

    /**
     * Reinicia el contador de procesos.
     */
    public void resetCounter() {
        this.processCounter = 1;
    }

    /**
     * Establece el contador de procesos en un valor específico.
     * @param counter valor inicial del contador
     */
    public void setCounter(int counter) {
        this.processCounter = counter;
    }
}
