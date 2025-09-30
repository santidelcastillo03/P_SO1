/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Representa el bloque de control de un proceso (PCB) con los datos básicos
 * necesarios para gestionar su ciclo de vida dentro del simulador.
 */
public class ProcessControlBlock {

	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

	private final int processId;
	private String processName;
	private ProcessState processState;
	// Context / hardware registers
	private int programCounter;
	private int memoryAddressRegister;

	// Scheduling-related
	private int totalInstructions;
	private boolean isIOBound;
	private int ioExceptionCycle; // cycle at which it will request IO (relative to start)
	private int ioDuration; // duration (in cycles) of the IO

	// Metrics
	private long creationTime;
	private long completionTime;

	public ProcessControlBlock(String processName) {
		this(processName, ProcessState.NUEVO);
	}

	public ProcessControlBlock(String processName, ProcessState initialState) {
		this.processId = ID_GENERATOR.getAndIncrement();
		// validate and assign directly to avoid calling overridable methods from ctor
		if (processName == null || processName.isBlank()) {
			throw new IllegalArgumentException("El nombre del proceso no puede ser nulo ni vacío");
		}
		this.processName = processName.trim();
		this.processState = Objects.requireNonNull(initialState, "El estado del proceso no puede ser nulo");
		this.programCounter = 0;
		this.memoryAddressRegister = 0;
		this.totalInstructions = 0;
		this.isIOBound = false;
		this.ioExceptionCycle = -1;
		this.ioDuration = 0;
		this.creationTime = System.currentTimeMillis();
		this.completionTime = -1L;
	}

	public int getProcessId() {
		return processId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		if (processName == null || processName.isBlank()) {
			throw new IllegalArgumentException("El nombre del proceso no puede ser nulo ni vacío");
		}
		this.processName = processName.trim();
	}

	public ProcessState getProcessState() {
		return processState;
	}

	public void setProcessState(ProcessState processState) {
		this.processState = Objects.requireNonNull(processState, "El estado del proceso no puede ser nulo");
	}

	// --- Context getters/setters ---
	public int getProgramCounter() {
		return programCounter;
	}

	public void setProgramCounter(int programCounter) {
		if (programCounter < 0) throw new IllegalArgumentException("programCounter no puede ser negativo");
		this.programCounter = programCounter;
	}

	public int getMemoryAddressRegister() {
		return memoryAddressRegister;
	}

	public void setMemoryAddressRegister(int memoryAddressRegister) {
		if (memoryAddressRegister < 0) throw new IllegalArgumentException("memoryAddressRegister no puede ser negativo");
		this.memoryAddressRegister = memoryAddressRegister;
	}

	// --- Scheduling getters/setters ---
	public int getTotalInstructions() {
		return totalInstructions;
	}

	public void setTotalInstructions(int totalInstructions) {
		if (totalInstructions < 0) throw new IllegalArgumentException("totalInstructions no puede ser negativo");
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
		if (ioExceptionCycle < -1) throw new IllegalArgumentException("ioExceptionCycle inválido");
		this.ioExceptionCycle = ioExceptionCycle;
	}

	public int getIoDuration() {
		return ioDuration;
	}

	public void setIoDuration(int ioDuration) {
		if (ioDuration < 0) throw new IllegalArgumentException("ioDuration no puede ser negativo");
		this.ioDuration = ioDuration;
	}

	// --- Metrics ---
	public long getCreationTime() {
		return creationTime;
	}

	public long getCompletionTime() {
		return completionTime;
	}

	public void markCompleted(long completionTimestamp) {
		if (completionTimestamp < creationTime) throw new IllegalArgumentException("completionTime no puede ser anterior a creationTime");
		this.completionTime = completionTimestamp;
	}

	@Override
	public String toString() {
	return "PCB{" +
		"id=" + processId +
		", nombre='" + processName + '\'' +
		", estado=" + processState +
		", PC=" + programCounter +
		", MAR=" + memoryAddressRegister +
		", instrTotales=" + totalInstructions +
		", isIOBound=" + isIOBound +
		", ioExcCycle=" + ioExceptionCycle +
		", ioDuration=" + ioDuration +
		", creado=" + creationTime +
		", completado=" + completionTime +
		'}';
	}
}
