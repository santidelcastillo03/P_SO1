/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import core.CPU;
import core.OperatingSystem;
import core.ProcessControlBlock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import scheduler.PolicyType;
import util.IOHandler;

/**
 * Ventana principal del simulador de sistemas operativos.
 * Se encarga de acomodar los paneles de control, colas, CPU y cronograma.
 *
 * @author santiagodelcastillo
 */
public class MainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());
    private static final String[] RANDOM_NAME_BASES = {
        "TAR", "PROC", "TRAB", "OP", "CALC", "LECT", "ESCR", "CONS", "ANAL", "COMP",
        "EJEC", "PROCES", "CARGA", "DESC", "TRANS", "VAL", "GEN", "ACT", "BUSQ", "ORD"
    };
    private static final AtomicInteger RANDOM_NAME_SEQUENCE = new AtomicInteger(1);
    private transient OperatingSystem operatingSystem;
    private final List<ProcessRequest> pendingProcesses = new ArrayList<>();
    private IOHandler managedIoHandler;
    private Thread managedIoThread;
    private CPU managedCpu;
    private OperatingSystem managedOperatingSystem;

    /**
     * Construye la ventana principal del simulador y prepara el layout base.
     */
    public MainFrame() {
        initComponents();
        setupControlBindings();
    }

    /**
     * Este método es invocado por el constructor para inicializar los componentes.
     * ADVERTENCIA: no modificar manualmente; el editor de formularios lo regenera.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        controlsPanel = new ui.ControlsPanel();
        centerPanel = new javax.swing.JPanel();
        chartPanel = new ui.ChartPanel();
        cpuPanel = new ui.CpuPanel();
        queuesPanel = new ui.QueuesPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Simulador de Sistemas Operativos");
        setMinimumSize(new java.awt.Dimension(1100, 720));
        getContentPane().setLayout(new java.awt.BorderLayout(12, 12));
        getContentPane().add(controlsPanel, java.awt.BorderLayout.NORTH);

        centerPanel.setOpaque(false);
        centerPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 8, 0);
        centerPanel.add(chartPanel, gridBagConstraints);

        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

        cpuPanel.setPreferredSize(new java.awt.Dimension(200, 10));
        getContentPane().add(cpuPanel, java.awt.BorderLayout.EAST);
        getContentPane().add(queuesPanel, java.awt.BorderLayout.LINE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Punto de entrada de la aplicación Swing.
     * @param args argumentos recibidos desde la línea de comandos (no utilizados)
     */
    public static void main(String args[]) {
        /* Configura el aspecto visual Nimbus */
        //<editor-fold defaultstate="collapsed" desc=" Código opcional para el look and feel ">
        /* Si Nimbus (introducido en Java SE 6) no está disponible, se mantiene el aspecto predeterminado.
         * Para más detalles visita http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Crea y muestra la ventana principal */
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    public void bindOperatingSystem(OperatingSystem operatingSystem) {
        if (this.operatingSystem == operatingSystem) {
            return;
        }
        OperatingSystem previous = this.operatingSystem;
        if (previous != null) {
            previous.setQueueListener(null);
            previous.setCpuListener(null);
        }
        if (previous != null
                && previous == managedOperatingSystem
                && operatingSystem != managedOperatingSystem) {
            shutdownManagedRuntime();
        }
        this.operatingSystem = operatingSystem;
        synchronized (pendingProcesses) {
            pendingProcesses.clear();
        }
        if (operatingSystem != null) {
            operatingSystem.setQueueListener(this::handleQueueUpdate);
            operatingSystem.setCpuListener(this::handleCpuUpdate);
            PolicyType policy = resolveActivePolicy(operatingSystem);
            controlsPanel.setControlsState(policy,
                    operatingSystem.getCycleDurationMillis(),
                    operatingSystem.getRoundRobinQuantum(),
                    operatingSystem.getFeedbackQuanta(),
                    operatingSystem.getMaxProcessesInMemory());
            if (operatingSystem == managedOperatingSystem && managedIoHandler != null) {
                managedIoHandler.setCycleDurationMillis(operatingSystem.getCycleDurationMillis());
            }
        } else {
            queuesPanel.updateQueueViews(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
            cpuPanel.updateCpuView(null, 0L, OperatingSystem.CpuMode.OS);
            controlsPanel.setControlsState(PolicyType.FCFS, 100L, 4, null, controlsPanel.getSelectedMaxMemoryValue());
        }
        refreshSimulationControls();
    }

    private void handleQueueUpdate(List<ProcessControlBlock> ready,
                                   List<ProcessControlBlock> blocked,
                                   List<ProcessControlBlock> finished,
                                   List<ProcessControlBlock> readySuspended,
                                   List<ProcessControlBlock> blockedSuspended) {
        queuesPanel.updateQueueViews(ready, blocked, finished, readySuspended, blockedSuspended);
    }

    private void handleCpuUpdate(ProcessControlBlock current,
                                 long clockCycle,
                                 OperatingSystem.CpuMode mode) {
        releasePendingProcesses(clockCycle);
        cpuPanel.updateCpuView(current, clockCycle, mode);
    }

    private void setupControlBindings() {
        controlsPanel.setPolicyChangeListener(this::handlePolicySelected);
        controlsPanel.setSpeedChangeListener(this::handleSpeedChanged);
        controlsPanel.setQuantumChangeListener(this::handleQuantumChanged);
        controlsPanel.setFeedbackQuantumListener(this::handleFeedbackQuantumChanged);
        controlsPanel.setProcessCreationListener(this::handleProcessCreation);
        controlsPanel.setMaxMemoryListener(this::handleMaxMemoryChanged);
        controlsPanel.setRandomProcessesListener(this::handleRandomProcessesRequested);
        controlsPanel.setStartListener(this::handleStartRequested);
        controlsPanel.setPauseListener(this::handlePauseRequested);
        controlsPanel.setResetListener(this::handleResetRequested);
    }

    private void handlePolicySelected(PolicyType policy) {
        if (operatingSystem == null || policy == null) {
            return;
        }
        operatingSystem.setSchedulingPolicy(policy);
        controlsPanel.setControlsState(policy,
                operatingSystem.getCycleDurationMillis(),
                operatingSystem.getRoundRobinQuantum(),
                operatingSystem.getFeedbackQuanta(),
                operatingSystem.getMaxProcessesInMemory());
    }

    private void handleSpeedChanged(long cycleDuration) {
        if (operatingSystem == null) {
            return;
        }
        operatingSystem.setCycleDurationMillis(cycleDuration);
        if (operatingSystem == managedOperatingSystem && managedIoHandler != null) {
            managedIoHandler.setCycleDurationMillis(cycleDuration);
        }
    }

    private void handleQuantumChanged(int quantum) {
        if (operatingSystem == null) {
            return;
        }
        operatingSystem.setRoundRobinQuantum(quantum);
    }

    private void handleFeedbackQuantumChanged(int[] quanta) {
        if (operatingSystem == null || quanta == null) {
            return;
        }
        operatingSystem.setFeedbackQuanta(quanta);
    }

    /**
     * Ajusta el límite de procesos residentes en memoria según la entrada del usuario.
     * @param limit nuevo tope de procesos en memoria principal
     */
    private void handleMaxMemoryChanged(int limit) {
        if (operatingSystem == null) {
            return;
        }
        try {
            operatingSystem.setMaxProcessesInMemory(limit);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.WARNING, "Límite de procesos inválido: {0}", limit);
        }
    }

    private void handleProcessCreation(ControlsPanel.ProcessFormData data) {
        if (data == null) {
            return;
        }
        if (operatingSystem == null) {
            initializeManagedRuntime();
        }
        if (operatingSystem == null) {
            return;
        }
        ProcessRequest request = new ProcessRequest(
                data.nombre,
                data.arribo,
                data.instrucciones,
                data.ioBound,
                data.ioCycle,
                data.ioDuration);
        scheduleProcess(request);
        refreshSimulationControls();
    }

    private PolicyType resolveActivePolicy(OperatingSystem os) {
        PolicyType type = os.getScheduler() != null ? os.getScheduler().getActivePolicyType() : null;
        return type != null ? type : PolicyType.FCFS;
    }

    /**
     * Atiende el clic del botón que genera procesos aleatorios y los programa de inmediato.
     */
    private void handleRandomProcessesRequested() {
        if (operatingSystem == null) {
            initializeManagedRuntime();
        }
        if (operatingSystem == null) {
            return;
        }
        int baseCycle = (int) Math.max(0L, Math.min(Integer.MAX_VALUE, operatingSystem.getGlobalClockCycle()));
        List<ProcessRequest> randomProcesses = generateRandomProcesses(20, baseCycle);
        for (ProcessRequest request : randomProcesses) {
            scheduleProcess(request);
        }
        logger.info(() -> String.format("Se agregaron %d procesos aleatorios al simulador", randomProcesses.size()));
        refreshSimulationControls();
    }

    /**
     * Genera procesos con atributos aleatorios controlados para diversificar la simulación.
     * @param count cantidad de procesos a crear
     * @param baseCycle ciclo mínimo de arribo permitido
     * @return lista con solicitudes de creación de procesos aleatorios
     */
    private List<ProcessRequest> generateRandomProcesses(int count, int baseCycle) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<ProcessRequest> requests = new ArrayList<>(Math.max(0, count));
        int safeBase = Math.max(0, Math.min(Integer.MAX_VALUE - 50, baseCycle));
        for (int i = 0; i < count; i++) {
            String baseName = RANDOM_NAME_BASES[random.nextInt(RANDOM_NAME_BASES.length)];
            int sequence = RANDOM_NAME_SEQUENCE.getAndIncrement();
            String nombre = baseName + sequence;
            int instrucciones = random.nextInt(5, 61);
            boolean ioBound = random.nextBoolean();
            int ioCycle = -1;
            int ioDuration = 0;
            if (ioBound) {
                ioCycle = random.nextInt(1, instrucciones);
                int remaining = Math.max(1, instrucciones - ioCycle);
                ioDuration = random.nextInt(1, Math.min(remaining + 1, 8));
            }
            int arrivalOffset = random.nextInt(0, 30);
            int arribo = safeBase + arrivalOffset;
            requests.add(new ProcessRequest(nombre, arribo, instrucciones, ioBound, ioCycle, ioDuration));
        }
        return requests;
    }

    /**
     * Gestiona la petición de inicio del reloj del simulador.
     */
    private void handleStartRequested() {
        if (operatingSystem == null) {
            initializeManagedRuntime();
        }
        if (operatingSystem == null) {
            showSimulationMessage("No hay un sistema operativo vinculado para iniciar la simulación.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (operatingSystem.isClockRunning()) {
            refreshSimulationControls();
            return;
        }
        try {
            operatingSystem.startSystemClock();
        } catch (IllegalStateException ex) {
            logger.log(Level.WARNING, "Fallo al iniciar la simulación", ex);
            showSimulationMessage("No se pudo iniciar la simulación: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
        refreshSimulationControls();
    }

    /**
     * Gestiona la solicitud de pausa sobre el reloj del simulador.
     */
    private void handlePauseRequested() {
        if (operatingSystem == null) {
            showSimulationMessage("No hay un sistema operativo vinculado para pausar.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!operatingSystem.isClockRunning()) {
            refreshSimulationControls();
            return;
        }
        operatingSystem.stopSystemClock();
        refreshSimulationControls();
    }

    /**
     * Gestiona la solicitud de reinicio del simulador limpiando colas y reloj.
     */
    private void handleResetRequested() {
        if (operatingSystem == null) {
            showSimulationMessage("No hay un sistema operativo vinculado para reiniciar.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        operatingSystem.resetSimulation();
        if (operatingSystem == managedOperatingSystem) {
            restartManagedRuntimeComponents();
        }
        synchronized (pendingProcesses) {
            pendingProcesses.clear();
        }
        refreshSimulationControls();
    }

    /**
     * Actualiza los estados de los botones de simulación según el contexto actual.
     */
    private void refreshSimulationControls() {
        boolean running = operatingSystem != null && operatingSystem.isClockRunning();
        boolean started = hasSimulationHistory();
        controlsPanel.setSimulationState(running, started);
    }

    /**
     * Determina si la simulación ya inició o posee procesos pendientes.
     * @return true cuando existen ciclos avanzados o procesos en colas
     */
    private boolean hasSimulationHistory() {
        if (operatingSystem == null) {
            return false;
        }
        return operatingSystem.isClockRunning()
                || operatingSystem.getGlobalClockCycle() > 0
                || operatingSystem.readyQueueSize() > 0
                || operatingSystem.blockedQueueSize() > 0
                || operatingSystem.finishedQueueSize() > 0
                || operatingSystem.readySuspendedQueueSize() > 0
                || operatingSystem.blockedSuspendedQueueSize() > 0;
    }

    /**
     * Muestra un mensaje emergente relacionado con la simulación.
     * @param message texto que se desea mostrar
     * @param messageType tipo de mensaje de JOptionPane
     */
    private void showSimulationMessage(String message, int messageType) {
        if (message == null || message.isBlank()) {
            return;
        }
        JOptionPane.showMessageDialog(this, message, "Simulación", messageType);
    }

    /**
     * Crea un sistema operativo manejado por la interfaz junto con su CPU e IOHandler.
     */
    private void initializeManagedRuntime() {
        if (operatingSystem != null) {
            return;
        }
        PolicyType desiredPolicy = controlsPanel.getSelectedPolicyType();
        long desiredSpeed = controlsPanel.getSelectedSpeedMillis();
        int desiredQuantum = controlsPanel.getSelectedQuantumValue();
        int[] desiredFeedback = controlsPanel.getSelectedFeedbackQuanta();
        int desiredMaxMemory = controlsPanel.getSelectedMaxMemoryValue();
        

        OperatingSystem os = new OperatingSystem();
        IOHandler handler = new IOHandler(os, desiredSpeed);
        Thread ioThread = new Thread(handler, "IOHandler-UI");
        ioThread.setDaemon(true);
        ioThread.start();
        CPU cpu = new CPU(os, handler);
        os.attachCpu(cpu);

        managedOperatingSystem = os;
        managedIoHandler = handler;
        managedIoThread = ioThread;
        managedCpu = cpu;

        bindOperatingSystem(os);
        applyConfigurationToOperatingSystem(desiredPolicy, desiredSpeed, desiredQuantum, desiredFeedback, desiredMaxMemory);
        refreshSimulationControls();
    }

    /**
     * Aplica la configuración deseada del panel de control al sistema operativo administrado.
     * @param policy política de planificación seleccionada
     * @param cycleDuration duración de ciclo en milisegundos
     * @param quantum quantum de Round Robin
     * @param feedbackQuanta arreglo de quantums para Feedback
     * @param maxMemoryLimit límite máximo de procesos en memoria principal
     */
    private void applyConfigurationToOperatingSystem(PolicyType policy,
                                                     long cycleDuration,
                                                     int quantum,
                                                     int[] feedbackQuanta,
                                                     int maxMemoryLimit) {
        if (operatingSystem == null) {
            return;
        }
        operatingSystem.setMaxProcessesInMemory(maxMemoryLimit);
        operatingSystem.setCycleDurationMillis(cycleDuration);
        if (operatingSystem == managedOperatingSystem && managedIoHandler != null) {
            managedIoHandler.setCycleDurationMillis(cycleDuration);
        }
        int[] appliedFeedback = feedbackQuanta;
        int[] currentFeedback = operatingSystem.getFeedbackQuanta();
        if (appliedFeedback == null || appliedFeedback.length != currentFeedback.length) {
            appliedFeedback = currentFeedback;
        }
        operatingSystem.setFeedbackQuanta(appliedFeedback);
        operatingSystem.setRoundRobinQuantum(quantum);
        operatingSystem.setSchedulingPolicy(policy);
        controlsPanel.setControlsState(policy, cycleDuration, quantum, appliedFeedback, maxMemoryLimit);
    }

    /**
     * Reinicia los componentes gestionados de I/O y CPU tras un reinicio del simulador.
     */
    private void restartManagedRuntimeComponents() {
        if (managedOperatingSystem == null || operatingSystem != managedOperatingSystem) {
            return;
        }
        stopManagedIoThread();

        PolicyType desiredPolicy = controlsPanel.getSelectedPolicyType();
        long desiredSpeed = controlsPanel.getSelectedSpeedMillis();
        int desiredQuantum = controlsPanel.getSelectedQuantumValue();
        int[] desiredFeedback = controlsPanel.getSelectedFeedbackQuanta();
        int desiredMaxMemory = controlsPanel.getSelectedMaxMemoryValue();

        IOHandler handler = new IOHandler(managedOperatingSystem, desiredSpeed);
        Thread ioThread = new Thread(handler, "IOHandler-UI");
        ioThread.setDaemon(true);
        ioThread.start();
        managedIoHandler = handler;
        managedIoThread = ioThread;
        managedCpu = new CPU(managedOperatingSystem, handler);
        managedOperatingSystem.attachCpu(managedCpu);
        applyConfigurationToOperatingSystem(desiredPolicy, desiredSpeed, desiredQuantum, desiredFeedback, desiredMaxMemory);
    }

    /**
     * Detiene el hilo de I/O administrado con una espera acotada.
     */
    private void stopManagedIoThread() {
        if (managedIoHandler != null) {
            managedIoHandler.stop();
        }
        if (managedIoThread != null) {
            try {
                managedIoThread.join(500L);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                logger.log(Level.WARNING, "Interrupción mientras se detenía el IOHandler", ex);
            }
        }
        managedIoHandler = null;
        managedIoThread = null;
    }

    /**
     * Libera los recursos internos cuando se reemplaza el sistema operativo administrado.
     */
    private void shutdownManagedRuntime() {
        if (managedOperatingSystem == null) {
            return;
        }
        managedOperatingSystem.stopSystemClock();
        stopManagedIoThread();
        managedCpu = null;
        managedOperatingSystem = null;
        synchronized (pendingProcesses) {
            pendingProcesses.clear();
        }
    }

    private void scheduleProcess(ProcessRequest request) {
        long currentCycle = operatingSystem.getGlobalClockCycle();
        if (request.arribo <= currentCycle) {
            createProcessNow(request);
        } else {
            synchronized (pendingProcesses) {
                pendingProcesses.add(request);
            }
            logger.info(() -> String.format("Proceso %s programado para arribo en ciclo %d", request.nombre, request.arribo));
        }
    }

    private void releasePendingProcesses(long currentCycle) {
        List<ProcessRequest> dueRequests = null;
        synchronized (pendingProcesses) {
            if (pendingProcesses.isEmpty()) {
                return;
            }
            Iterator<ProcessRequest> iterator = pendingProcesses.iterator();
            while (iterator.hasNext()) {
                ProcessRequest request = iterator.next();
                if (request.arribo <= currentCycle) {
                    if (dueRequests == null) {
                        dueRequests = new ArrayList<>();
                    }
                    dueRequests.add(request);
                    iterator.remove();
                }
            }
        }
        if (dueRequests == null) {
            return;
        }
        for (ProcessRequest request : dueRequests) {
            createProcessNow(request);
        }
    }

    private void createProcessNow(ProcessRequest request) {
        if (operatingSystem == null) {
            return;
        }
        ProcessControlBlock pcb = operatingSystem.createProcess(
                request.nombre,
                request.instrucciones,
                request.ioBound,
                request.ioCycle,
                request.ioDuration);
        logger.info(() -> String.format("Proceso creado: %s (#%d) [arribo=%d]",
                pcb.getProcessName(),
                pcb.getProcessId(),
                request.arribo));
    }

    private static final class ProcessRequest {
        final String nombre;
        final int arribo;
        final int instrucciones;
        final boolean ioBound;
        final int ioCycle;
        final int ioDuration;

        ProcessRequest(String nombre,
                        int arribo,
                        int instrucciones,
                        boolean ioBound,
                        int ioCycle,
                        int ioDuration) {
            this.nombre = nombre;
            this.arribo = arribo;
            this.instrucciones = instrucciones;
            this.ioBound = ioBound;
            this.ioCycle = ioCycle;
            this.ioDuration = ioDuration;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private ui.ChartPanel chartPanel;
    private ui.ControlsPanel controlsPanel;
    private ui.CpuPanel cpuPanel;
    private ui.QueuesPanel queuesPanel;
    // End of variables declaration//GEN-END:variables
}
