/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import core.OperatingSystem;
import core.ProcessControlBlock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import scheduler.PolicyType;

/**
 * Ventana principal del simulador de sistemas operativos.
 * Se encarga de acomodar los paneles de control, colas, CPU y cronograma.
 *
 * @author santiagodelcastillo
 */
public class MainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());
    private static final Map<String, List<ProcessRequest>> SCENARIOS = buildScenarioCatalog();
    private transient OperatingSystem operatingSystem;
    private final List<ProcessRequest> pendingProcesses = new ArrayList<>();

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
    // <editor-fold defaultstate="collapsed" desc="Código generado">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlsPanel = new ui.ControlsPanel();
        queuesPanel = new ui.QueuesPanel();
        cpuPanel = new ui.CpuPanel();
        chartPanel = new ui.ChartPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Simulador de Sistemas Operativos");
        setMinimumSize(new java.awt.Dimension(1100, 720));
        getContentPane().setLayout(new java.awt.BorderLayout(12, 12));

        getContentPane().add(controlsPanel, java.awt.BorderLayout.NORTH);

        javax.swing.JPanel centerPanel = new javax.swing.JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new java.awt.GridBagLayout());

        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.45;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 8, 8);
        centerPanel.add(queuesPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.55;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 8, 0);
        centerPanel.add(chartPanel, gridBagConstraints);

        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

        cpuPanel.setPreferredSize(new java.awt.Dimension(280, 10));
        getContentPane().add(cpuPanel, java.awt.BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
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
        if (this.operatingSystem != null) {
            this.operatingSystem.setQueueListener(null);
            this.operatingSystem.setCpuListener(null);
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
                    operatingSystem.getRoundRobinQuantum());
        } else {
            queuesPanel.updateQueueViews(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
            cpuPanel.updateCpuView(null, 0L, OperatingSystem.CpuMode.OS);
            controlsPanel.setControlsState(PolicyType.FCFS, 100L, 4);
        }
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
        controlsPanel.setProcessCreationListener(this::handleProcessCreation);
        controlsPanel.setScenarioLoadListener(this::handleScenarioLoad);
    }

    private void handlePolicySelected(PolicyType policy) {
        if (operatingSystem == null || policy == null) {
            return;
        }
        operatingSystem.setSchedulingPolicy(policy);
        controlsPanel.setControlsState(policy,
                operatingSystem.getCycleDurationMillis(),
                operatingSystem.getRoundRobinQuantum());
    }

    private void handleSpeedChanged(long cycleDuration) {
        if (operatingSystem == null) {
            return;
        }
        operatingSystem.setCycleDurationMillis(cycleDuration);
    }

    private void handleQuantumChanged(int quantum) {
        if (operatingSystem == null) {
            return;
        }
        operatingSystem.setRoundRobinQuantum(quantum);
    }

    private void handleProcessCreation(ControlsPanel.ProcessFormData data) {
        if (operatingSystem == null || data == null) {
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
    }

    private PolicyType resolveActivePolicy(OperatingSystem os) {
        PolicyType type = os.getScheduler() != null ? os.getScheduler().getActivePolicyType() : null;
        return type != null ? type : PolicyType.FCFS;
    }

    private void handleScenarioLoad(String scenarioName) {
        if (operatingSystem == null) {
            return;
        }
        List<ProcessRequest> scenario = SCENARIOS.get(scenarioName);
        if (scenario == null || scenario.isEmpty()) {
            logger.warning(() -> "No se encontró el escenario: " + scenarioName);
            return;
        }
        synchronized (pendingProcesses) {
            pendingProcesses.clear();
        }
        long currentCycle = operatingSystem.getGlobalClockCycle();
        for (ProcessRequest request : scenario) {
            if (request.arribo <= currentCycle) {
                createProcessNow(request);
            } else {
                synchronized (pendingProcesses) {
                    pendingProcesses.add(request);
                }
            }
        }
        logger.info(() -> String.format("Escenario '%s' cargado con %d procesos", scenarioName, scenario.size()));
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

    private static Map<String, List<ProcessRequest>> buildScenarioCatalog() {
        Map<String, List<ProcessRequest>> scenarios = new HashMap<>();
        scenarios.put("FCFS", List.of(
                new ProcessRequest("FCFS-Largo", 0, 32, false, -1, 0),
                new ProcessRequest("FCFS-IO-A", 0, 14, true, 4, 3),
                new ProcessRequest("FCFS-Corto", 1, 6, false, -1, 0),
                new ProcessRequest("FCFS-IO-B", 1, 9, true, 3, 2),
                new ProcessRequest("FCFS-Extra-1", 2, 8, false, -1, 0),
                new ProcessRequest("FCFS-Extra-2", 2, 7, false, -1, 0)));
        scenarios.put("SPN", List.of(
                new ProcessRequest("SPN-Base", 0, 20, false, -1, 0),
                new ProcessRequest("SPN-Corto-IO", 0, 5, true, 2, 2),
                new ProcessRequest("SPN-Flash", 1, 3, false, -1, 0),
                new ProcessRequest("SPN-Rapido", 2, 4, false, -1, 0),
                new ProcessRequest("SPN-Med-IO", 2, 7, true, 2, 3),
                new ProcessRequest("SPN-Largo", 3, 18, false, -1, 0)));
        scenarios.put("HRRN", List.of(
                new ProcessRequest("HRRN-Largo", 0, 28, false, -1, 0),
                new ProcessRequest("HRRN-Medio", 1, 12, false, -1, 0),
                new ProcessRequest("HRRN-Corto-A", 6, 5, false, -1, 0),
                new ProcessRequest("HRRN-Corto-B", 10, 4, false, -1, 0),
                new ProcessRequest("HRRN-Corto-C", 14, 3, false, -1, 0)));
        scenarios.put("SRTF", List.of(
                new ProcessRequest("SRTF-Largo", 0, 30, false, -1, 0),
                new ProcessRequest("SRTF-IO-1", 1, 11, true, 3, 3),
                new ProcessRequest("SRTF-Flash", 2, 3, false, -1, 0),
                new ProcessRequest("SRTF-Medio", 2, 12, false, -1, 0),
                new ProcessRequest("SRTF-IO-2", 3, 8, true, 2, 2),
                new ProcessRequest("SRTF-Extra", 3, 5, false, -1, 0)));
        scenarios.put("Round Robin", List.of(
                new ProcessRequest("RR-CPU-Pesado", 0, 22, false, -1, 0),
                new ProcessRequest("RR-IO-1", 0, 13, true, 4, 3),
                new ProcessRequest("RR-Medio", 1, 12, false, -1, 0),
                new ProcessRequest("RR-IO-2", 1, 10, true, 3, 2),
                new ProcessRequest("RR-Ligero", 2, 6, false, -1, 0),
                new ProcessRequest("RR-Refuerzo", 2, 8, false, -1, 0)));
        scenarios.put("Feedback", List.of(
                new ProcessRequest("FB-Largo", 0, 12, false, -1, 0),
                new ProcessRequest("FB-Corto-A", 0, 4, false, -1, 0),
                new ProcessRequest("FB-Medio", 1, 6, false, -1, 0)));
        return scenarios;
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

    // Declaración de variables - no modificar//GEN-BEGIN:variables
    private ui.ChartPanel chartPanel;
    private ui.ControlsPanel controlsPanel;
    private ui.CpuPanel cpuPanel;
    private ui.QueuesPanel queuesPanel;
    // Fin de la declaración de variables//GEN-END:variables
}
