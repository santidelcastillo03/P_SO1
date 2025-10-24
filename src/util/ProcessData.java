/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 * Clase de transferencia de datos para serializar/deserializar parámetros de procesos.
 * Contiene los parámetros necesarios para crear un proceso en futuras simulaciones.
 *
 * @author Santiago
 */
public class ProcessData {

    private String processName;
    private int totalInstructions;
    private boolean isIOBound;
    private int ioExceptionCycle;
    private int ioDuration;
    private long arrivalCycle;

    /**
     * Constructor vacío para deserialización.
     */
    public ProcessData() {
    }

    /**
     * Constructor completo con todos los parámetros de un proceso.
     */
    public ProcessData(String processName, int totalInstructions, boolean isIOBound,
                      int ioExceptionCycle, int ioDuration, long arrivalCycle) {
        this.processName = processName;
        this.totalInstructions = totalInstructions;
        this.isIOBound = isIOBound;
        this.ioExceptionCycle = ioExceptionCycle;
        this.ioDuration = ioDuration;
        this.arrivalCycle = arrivalCycle;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getTotalInstructions() {
        return totalInstructions;
    }

    public void setTotalInstructions(int totalInstructions) {
        this.totalInstructions = totalInstructions;
    }

    public boolean isIOBound() {
        return isIOBound;
    }

    public void setIOBound(boolean isIOBound) {
        this.isIOBound = isIOBound;
    }

    public int getIoExceptionCycle() {
        return ioExceptionCycle;
    }

    public void setIoExceptionCycle(int ioExceptionCycle) {
        this.ioExceptionCycle = ioExceptionCycle;
    }

    public int getIoDuration() {
        return ioDuration;
    }

    public void setIoDuration(int ioDuration) {
        this.ioDuration = ioDuration;
    }

    public long getArrivalCycle() {
        return arrivalCycle;
    }

    public void setArrivalCycle(long arrivalCycle) {
        this.arrivalCycle = arrivalCycle;
    }
}
