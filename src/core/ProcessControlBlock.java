/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

// Paquete principal del núcleo del simulador
package core;


import java.util.Objects; // Utilidad para validaciones de nulidad
import java.util.concurrent.atomic.AtomicInteger; // Para generación segura de IDs únicos

/**
 * Representa el bloque de control de un proceso (PCB) con los datos básicos
 * necesarios para gestionar su ciclo de vida dentro del simulador.
 * <p>
 * Cada proceso tiene un identificador único, nombre, estado, registros de contexto,
 * información de planificación (scheduling), y métricas de creación/finalización.
 * </p>
 */

public class ProcessControlBlock {


	/**
	 * Generador atómico para IDs únicos de procesos.
	 */
	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);


	/** Identificador único del proceso. */
	private final int processId;
	/** Nombre del proceso. */
	private String processName;
	/** Estado actual del proceso (enum). */
	private ProcessState processState;
	// --- Registros de contexto/hardware ---
	/** Contador de programa (PC): instrucción actual. */
	private int programCounter;
	/** Registro de dirección de memoria (MAR). */
	private int memoryAddressRegister;

	// --- Datos de planificación (scheduling) ---
	/** Número total de instrucciones a ejecutar. */
	private int totalInstructions;
	/** Indica si el proceso es I/O bound. */
	private boolean isIOBound;
	/** Ciclo en el que se solicita I/O (relativo al inicio). */
	private int ioExceptionCycle;
	/** Duración del evento de I/O en ciclos. */
	private int ioDuration;

	// --- Métricas ---
	/** Timestamp de creación del proceso. */
	private long creationTime;
	/** Timestamp de finalización del proceso. */
	private long completionTime;
	/** Ciclo en el que el proceso ingresó a la cola de listos. */
	private long readyQueueArrivalTime;


	/**
	 * Constructor principal. Crea un proceso en estado NUEVO.
	 * @param processName Nombre del proceso
	 */
	public ProcessControlBlock(String processName) {
		this(processName, ProcessState.NUEVO);
	}

	/**
	 * Constructor completo. Permite especificar el estado inicial.
	 * @param processName Nombre del proceso
	 * @param initialState Estado inicial del proceso
	 */
	public ProcessControlBlock(String processName, ProcessState initialState) {
		this.processId = ID_GENERATOR.getAndIncrement();
		// Validación de nombre y estado
		if (processName == null || processName.isBlank()) {
			throw new IllegalArgumentException("El nombre del proceso no puede ser nulo ni vacío");
		}
		this.processName = processName.trim();
		this.processState = Objects.requireNonNull(initialState, "El estado del proceso no puede ser nulo");
		// Inicialización de registros y métricas
		this.programCounter = 0;
		this.memoryAddressRegister = 0;
		this.totalInstructions = 0;
		this.isIOBound = false;
		this.ioExceptionCycle = -1;
		this.ioDuration = 0;
		this.creationTime = System.currentTimeMillis();
		this.completionTime = -1L;
		this.readyQueueArrivalTime = -1L;
	}


	/**
	 * Devuelve el ID único del proceso.
	 */
	public int getProcessId() {
		return processId;
	}


	/**
	 * Devuelve el nombre del proceso.
	 */
	public String getProcessName() {
		return processName;
	}


	/**
	 * Asigna un nuevo nombre al proceso.
	 * @param processName Nombre válido (no nulo ni vacío)
	 */
	public void setProcessName(String processName) {
		if (processName == null || processName.isBlank()) {
			throw new IllegalArgumentException("El nombre del proceso no puede ser nulo ni vacío");
		}
		this.processName = processName.trim();
	}


	/**
	 * Devuelve el estado actual del proceso.
	 */
	public ProcessState getProcessState() {
		return processState;
	}


	/**
	 * Cambia el estado del proceso.
	 * @param processState Nuevo estado (no nulo)
	 */
	public void setProcessState(ProcessState processState) {
		this.processState = Objects.requireNonNull(processState, "El estado del proceso no puede ser nulo");
	}

	/**
	 * Registra el ciclo en el que el proceso entró a la cola de listos.
	 * @param cycle número de ciclo global
	 */
	public void markReadyQueueArrival(long cycle) {
		readyQueueArrivalTime = Math.max(0L, cycle);
	}

	/**
	 * Limpia la marca de llegada a ready (cuando sale de la cola).
	 */
	public void clearReadyQueueArrival() {
		readyQueueArrivalTime = -1L;
	}

	/**
	 * Devuelve el ciclo en el que el proceso entró a ready.
	 * @return ciclo registrado o -1 si no está marcado
	 */
	public long getReadyQueueArrivalTime() {
		return readyQueueArrivalTime;
	}


	// --- Getters y setters de contexto ---

	/**
	 * Devuelve el valor actual del contador de programa (PC).
	 */
	public int getProgramCounter() {
		return programCounter;
	}


	/**
	 * Asigna el valor del contador de programa (PC).
	 * @param programCounter Valor no negativo
	 */
	public void setProgramCounter(int programCounter) {
		if (programCounter < 0) throw new IllegalArgumentException("programCounter no puede ser negativo");
		this.programCounter = programCounter;
	}


	/**
	 * Devuelve el valor del registro de dirección de memoria (MAR).
	 */
	public int getMemoryAddressRegister() {
		return memoryAddressRegister;
	}


	/**
	 * Asigna el valor del registro de dirección de memoria (MAR).
	 * @param memoryAddressRegister Valor no negativo
	 */
	public void setMemoryAddressRegister(int memoryAddressRegister) {
		if (memoryAddressRegister < 0) throw new IllegalArgumentException("memoryAddressRegister no puede ser negativo");
		this.memoryAddressRegister = memoryAddressRegister;
	}


	// --- Getters y setters de planificación ---

	/**
	 * Devuelve el número total de instrucciones del proceso.
	 */
	public int getTotalInstructions() {
		return totalInstructions;
	}


	/**
	 * Asigna el número total de instrucciones.
	 * @param totalInstructions Valor no negativo
	 */
	public void setTotalInstructions(int totalInstructions) {
		if (totalInstructions < 0) throw new IllegalArgumentException("totalInstructions no puede ser negativo");
		this.totalInstructions = totalInstructions;
	}

	/**
	 * Calcula el tiempo de espera acumulado usando el ciclo global actual.
	 * @param currentCycle ciclo global del sistema
	 * @return tiempo de espera en ciclos
	 */
	public long getWaitingTime(long currentCycle) {
		if (readyQueueArrivalTime < 0) {
			return 0L;
		}
		long waiting = currentCycle - readyQueueArrivalTime;
		return waiting < 0 ? 0L : waiting;
	}

	/**
	 * Calcula el índice de respuesta (HRRN) con base en el tiempo de espera y duración total.
	 * @param currentCycle ciclo global del sistema
	 * @return ratio de respuesta (>=1)
	 */
	public double getResponseRatio(long currentCycle) {
		long waiting = getWaitingTime(currentCycle);
		if (totalInstructions <= 0) {
			return 1.0 + waiting;
		}
		return 1.0 + (waiting / (double) totalInstructions);
	}


	/**
	 * Indica si el proceso es I/O bound.
	 */
	public boolean isIOBound() {
		return isIOBound;
	}


	/**
	 * Marca el proceso como I/O bound o no.
	 * @param isIOBound true si es I/O bound
	 */
	public void setIOBound(boolean isIOBound) {
		this.isIOBound = isIOBound;
	}


	/**
	 * Devuelve el ciclo en el que se solicita I/O.
	 */
	public int getIoExceptionCycle() {
		return ioExceptionCycle;
	}


	/**
	 * Asigna el ciclo de solicitud de I/O.
	 * @param ioExceptionCycle Ciclo (>= -1)
	 */
	public void setIoExceptionCycle(int ioExceptionCycle) {
		if (ioExceptionCycle < -1) throw new IllegalArgumentException("ioExceptionCycle inválido");
		this.ioExceptionCycle = ioExceptionCycle;
	}


	/**
	 * Devuelve la duración del evento de I/O en ciclos.
	 */
	public int getIoDuration() {
		return ioDuration;
	}


	/**
	 * Asigna la duración del evento de I/O.
	 * @param ioDuration Duración en ciclos (>= 0)
	 */
	public void setIoDuration(int ioDuration) {
		if (ioDuration < 0) throw new IllegalArgumentException("ioDuration no puede ser negativo");
		this.ioDuration = ioDuration;
	}


	// --- Getters y setters de métricas ---

	/**
	 * Devuelve el timestamp de creación del proceso.
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * Devuelve el timestamp de finalización del proceso.
	 */
	public long getCompletionTime() {
		return completionTime;
	}

	/**
	 * Marca el proceso como completado y registra el timestamp de finalización.
	 * @param completionTimestamp Timestamp de finalización (>= creationTime)
	 */
	public void markCompleted(long completionTimestamp) {
		if (completionTimestamp < creationTime) throw new IllegalArgumentException("completionTime no puede ser anterior a creationTime");
		this.completionTime = completionTimestamp;
	}

	/**
	 * Devuelve una representación en String del PCB para depuración.
	 */
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
