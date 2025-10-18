/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java para editar este template
 */
package scheduler;

import core.ProcessControlBlock;
import datastructures.CustomQueue;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Scheduler centraliza la selección de procesos listos utilizando una política configurable.
 * Permite registrar múltiples políticas e intercambiarlas en tiempo de ejecución.
 */
public class Scheduler {

    /** Mapa de políticas registradas indexadas por tipo. */
    private final Map<PolicyType, SchedulingPolicy> registeredPolicies;
    /** Política actualmente activa. */
    private SchedulingPolicy activePolicy;
    /** Tipo asociado a la política activa (si proviene del catálogo). */
    private PolicyType activePolicyType;

    /**
     * Construye un planificador con política FCFS por defecto y registra las políticas conocidas.
     */
    public Scheduler() {
        this(new FCFS());
        registerDefaultPolicies();
        this.activePolicyType = PolicyType.FCFS;
    }

    /**
     * Construye un planificador con la política inicial indicada.
     * @param initialPolicy política de planificación inicial
     */
    public Scheduler(SchedulingPolicy initialPolicy) {
        this.registeredPolicies = new EnumMap<>(PolicyType.class);
        this.activePolicy = Objects.requireNonNull(initialPolicy, "La política inicial no puede ser nula");
    }

    /**
     * Selecciona el siguiente proceso listo soportado por la política activa.
     * @param readyQueue cola de procesos listos sobre la que se decide
     * @param currentProcess proceso actualmente en ejecución (puede ser null)
     * @return próximo proceso a ejecutar o null si la cola está vacía
     */
    public ProcessControlBlock selectNextProcess(CustomQueue<ProcessControlBlock> readyQueue,
                                                 ProcessControlBlock currentProcess) {
        if (activePolicy == null || readyQueue == null) {
            return null;
        }
        return activePolicy.selectNextProcess(readyQueue, currentProcess);
    }

    /**
     * Registra una política en el catálogo interno para facilitar su reutilización.
     * @param type identificador de la política
     * @param policy implementación concreta a registrar
     */
    public void registerPolicy(PolicyType type, SchedulingPolicy policy) {
        Objects.requireNonNull(type, "El tipo de política no puede ser nulo");
        Objects.requireNonNull(policy, "La política a registrar no puede ser nula");
        registeredPolicies.put(type, policy);
    }

    /**
     * Cambia la política activa tomando la implementación registrada para el tipo indicado.
     * @param type tipo de política objetivo
     */
    public void setPolicy(PolicyType type) {
        SchedulingPolicy policy = registeredPolicies.get(type);
        if (policy == null) {
            throw new IllegalArgumentException("No existe una política registrada para " + type);
        }
        this.activePolicy = policy;
        this.activePolicyType = type;
    }

    /**
     * Permite cambiar la política activa en tiempo de ejecución con una implementación específica.
     * @param policy nueva política que debe utilizar el planificador
     */
    public void setPolicy(SchedulingPolicy policy) {
        this.activePolicy = Objects.requireNonNull(policy, "La política activa no puede ser nula");
        this.activePolicyType = null;
    }

    /**
     * Devuelve el tipo de política actualmente asociado, si proviene del catálogo estándar.
     * @return tipo activo o null si la política fue inyectada manualmente
     */
    public PolicyType getActivePolicyType() {
        return activePolicyType;
    }

    /**
     * Obtiene la implementación de la política activa.
     * @return política actualmente utilizada
     */
    public SchedulingPolicy getActivePolicy() {
        return activePolicy;
    }

    /**
     * Registra las políticas básicas ofrecidas por el simulador.
     */
    private void registerDefaultPolicies() {
        registerPolicy(PolicyType.FCFS, activePolicy);
        registerPolicy(PolicyType.ROUND_ROBIN, new RoundRobin());
        registerPolicy(PolicyType.SPN, new SPN());
        registerPolicy(PolicyType.SRT, new SRTF());
        registerPolicy(PolicyType.HRRN, new PriorityNP());
        registerPolicy(PolicyType.FEEDBACK, new PriorityP());
    }
}
