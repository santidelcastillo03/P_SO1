/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

// Paquete principal del núcleo del simulador
package core;

/**
 * Enum que representa los posibles estados de un proceso en el simulador de sistema operativo.
 * <ul>
 *   <li><b>NUEVO</b>: Proceso recién creado, aún no admitido en memoria principal.</li>
 *   <li><b>LISTO</b>: Proceso preparado para ejecutarse, esperando asignación de CPU.</li>
 *   <li><b>EJECUCION</b>: Proceso actualmente en ejecución en la CPU.</li>
 *   <li><b>BLOQUEADO</b>: Proceso esperando un evento externo (por ejemplo, I/O).</li>
 *   <li><b>TERMINADO</b>: Proceso que ha finalizado su ejecución.</li>
 *   <li><b>LISTO_SUSPENDIDO</b>: Proceso listo pero suspendido (swap out).</li>
 *   <li><b>BLOQUEADO_SUSPENDIDO</b>: Proceso bloqueado y suspendido (swap out).</li>
 * </ul>
 * @author santiagodelcastillo
 */
public enum ProcessState {
	/** Proceso recién creado, aún no admitido en memoria principal. */
	NUEVO,
	/** Proceso preparado para ejecutarse, esperando asignación de CPU. */
	LISTO,
	/** Proceso actualmente en ejecución en la CPU. */
	EJECUCION,
	/** Proceso esperando un evento externo (por ejemplo, I/O). */
	BLOQUEADO,
	/** Proceso que ha finalizado su ejecución. */
	TERMINADO,
	/** Proceso listo pero suspendido (swap out). */
	LISTO_SUSPENDIDO,
	/** Proceso bloqueado y suspendido (swap out). */
	BLOQUEADO_SUSPENDIDO;
}
