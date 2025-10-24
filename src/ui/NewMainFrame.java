/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import core.CPU;
import core.OperatingSystem;
import core.ProcessControlBlock;
import scheduler.PolicyType;
import util.IOHandler;
import util.RandomProcessGenerator;
import datastructures.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author angel
 */
public class NewMainFrame extends javax.swing.JFrame {

    private static class PendingProcess {
        ProcessControlBlock pcb;
        long arrivalCycle;

        PendingProcess(ProcessControlBlock pcb, long arrivalCycle) {
            this.pcb = pcb;
            this.arrivalCycle = arrivalCycle;
        }
    }

    private OperatingSystem operatingSystem;
    private CPU cpu;
    private IOHandler ioHandler;
    private Thread ioThread;
    private Thread arrivalCheckerThread;
    private boolean internalPolicyUpdate;
    private ArrayList<PendingProcess> pendingProcesses;
    private RandomProcessGenerator processGenerator;
    private static final String[] POLICY_OPTIONS = {
        "FCFS",
        "Round Robin",
        "SPN",
        "SRTF",
        "HRRN",
        "Feedback"
    };

    /**
     * Creates new form NewMainFrame
     */
    public NewMainFrame() {
        pendingProcesses = new ArrayList<>();
        processGenerator = new RandomProcessGenerator();
        initializeSimulationComponents();
        initComponents();
        configureSpinners();
        configurePolicySelector();
        configureSpeedSlider();
        updateSimulationControls();
    }

    private void configureSpinners() {
        RRQuantumSpinner.setModel(new SpinnerNumberModel(operatingSystem.getRoundRobinQuantum(), 1, 20, 1));
        int[] feedbackValues = operatingSystem.getFeedbackQuantaSnapshot();
        level0Spinner.setModel(new SpinnerNumberModel(feedbackValues[0], 1, 50, 1));
        level1Spinner.setModel(new SpinnerNumberModel(feedbackValues[1], 1, 50, 1));
        level2Spinner.setModel(new SpinnerNumberModel(feedbackValues[2], 1, 50, 1));
        level3Spinner.setModel(new SpinnerNumberModel(feedbackValues[3], 1, 50, 1));
        maxMemorySpinner.setModel(new SpinnerNumberModel(operatingSystem.getMaxProcessesInMemory(), 1, 20, 1));
        arrivalSpinner.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        instructionSpinner.setModel(new SpinnerNumberModel(10, 1, 1000, 1));
        ioCycleSpinner.setModel(new SpinnerNumberModel(-1, -1, 1000, 1));
        ioDurationSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        RRQuantumSpinner.setEnabled(false);
        setFeedbackSpinnersEnabled(false);
        configureMaxMemorySpinner();
        configureProcessTypeSelector();
    }

    private void configurePolicySelector() {
        internalPolicyUpdate = true;
        policySelector.setModel(new DefaultComboBoxModel<>(POLICY_OPTIONS));
        policySelector.setSelectedItem(POLICY_OPTIONS[0]);
        internalPolicyUpdate = false;
        PolicyType initialPolicy = resolvePolicy(POLICY_OPTIONS[0]);
        updatePolicyControls(initialPolicy);
        applyPolicySelection(initialPolicy);
    }

    private void configureSpeedSlider() {
        speedSlider.setMinimum(0);
        speedSlider.setMaximum(2000);
        long currentCycleDuration = operatingSystem.getCycleDurationMillis();
        int initialValue = (int) Math.max(0L, Math.min(2000L, currentCycleDuration));
        speedSlider.setValue(initialValue);
        operatingSystem.setCycleDurationMillis(initialValue);
        ioHandler.setCycleDurationMillis(initialValue);
        updateSpeedLabel(initialValue);
        speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                int value = speedSlider.getValue();
                operatingSystem.setCycleDurationMillis(value);
                ioHandler.setCycleDurationMillis(value);
                updateSpeedLabel(value);
            }
        });
    }

    private void updateSpeedLabel(int value) {
        msLabel.setText(value + " ms");
    }

    private void configureMaxMemorySpinner() {
        maxMemorySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                int value = ((Number) maxMemorySpinner.getValue()).intValue();
                try {
                    operatingSystem.setMaxProcessesInMemory(value);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(NewMainFrame.this, ex.getMessage(), "Valor inválido", JOptionPane.ERROR_MESSAGE);
                    maxMemorySpinner.setValue(operatingSystem.getMaxProcessesInMemory());
                }
            }
        });
    }

    private void configureProcessTypeSelector() {
        processType.setModel(new DefaultComboBoxModel<>(new String[] { "CPU-bound", "I/O-bound" }));
        processType.setSelectedIndex(0);
        processType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateProcessTypeFields();
            }
        });
        updateProcessTypeFields();
    }

    private void updateProcessTypeFields() {
        boolean isIOBound = "I/O-bound".equals(processType.getSelectedItem());
        ioCycleSpinner.setEnabled(isIOBound);
        ioDurationSpinner.setEnabled(isIOBound);
        if (!isIOBound) {
            ioCycleSpinner.setValue(-1);
            ioDurationSpinner.setValue(0);
        } else {
            if (((Number) ioCycleSpinner.getValue()).intValue() == -1) {
                ioCycleSpinner.setValue(5);
            }
            if (((Number) ioDurationSpinner.getValue()).intValue() == 0) {
                ioDurationSpinner.setValue(3);
            }
        }
    }

    private void updatePolicyControls(PolicyType policyType) {
        boolean roundRobin = policyType == PolicyType.ROUND_ROBIN;
        boolean feedback = policyType == PolicyType.FEEDBACK;
        RRQuantumSpinner.setEnabled(roundRobin);
        setFeedbackSpinnersEnabled(feedback);
    }

    private void setFeedbackSpinnersEnabled(boolean enabled) {
        level0Spinner.setEnabled(enabled);
        level1Spinner.setEnabled(enabled);
        level2Spinner.setEnabled(enabled);
        level3Spinner.setEnabled(enabled);
    }

    private int[] collectFeedbackQuanta() {
        return new int[] {
            ((Number) level0Spinner.getValue()).intValue(),
            ((Number) level1Spinner.getValue()).intValue(),
            ((Number) level2Spinner.getValue()).intValue(),
            ((Number) level3Spinner.getValue()).intValue()
        };
    }

    private void applyPolicySelection(PolicyType policyType) {
        try {
            if (policyType == PolicyType.ROUND_ROBIN) {
                int quantum = ((Number) RRQuantumSpinner.getValue()).intValue();
                operatingSystem.setRoundRobinQuantum(quantum);
            }
            if (policyType == PolicyType.FEEDBACK) {
                int[] feedbackValues = collectFeedbackQuanta();
                operatingSystem.setFeedbackQuanta(feedbackValues[0], feedbackValues[1], feedbackValues[2], feedbackValues[3]);
            }
            operatingSystem.setSchedulingPolicy(policyType);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Configuración inválida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private PolicyType resolvePolicy(String label) {
        if (label == null) {
            return PolicyType.FCFS;
        }
        switch (label) {
            case "Round Robin":
                return PolicyType.ROUND_ROBIN;
            case "SPN":
                return PolicyType.SPN;
            case "SRTF":
                return PolicyType.SRT;
            case "HRRN":
                return PolicyType.HRRN;
            case "Feedback":
                return PolicyType.FEEDBACK;
            default:
                return PolicyType.FCFS;
        }
    }

    private void initializeSimulationComponents() {
        operatingSystem = new OperatingSystem();
        int speedValue = resolveCurrentSpeedSelection();
        ioHandler = new IOHandler(operatingSystem, speedValue);
        cpu = new CPU(operatingSystem, ioHandler);
        operatingSystem.attachCpu(cpu);
        ioThread = null;
    }

    private int resolveCurrentSpeedSelection() {
        return speedSlider != null ? speedSlider.getValue() : 100;
    }

    private void ensureIoThread() {
        if (ioHandler == null) {
            return;
        }
        if (ioThread == null || !ioThread.isAlive()) {
            ioThread = new Thread(ioHandler, "IOHandler-Thread");
            ioThread.setDaemon(true);
            ioThread.start();
        }
    }

    private void startArrivalChecker() {
        if (arrivalCheckerThread != null && arrivalCheckerThread.isAlive()) {
            return;
        }
        arrivalCheckerThread = new Thread(this::runArrivalChecker, "ArrivalChecker-Thread");
        arrivalCheckerThread.setDaemon(true);
        arrivalCheckerThread.start();
    }

    private void runArrivalChecker() {
        while (!Thread.currentThread().isInterrupted() && operatingSystem.isClockRunning()) {
            try {
                long currentCycle = operatingSystem.getGlobalClockCycle();
                checkPendingArrivals(currentCycle);
                Thread.sleep(Math.max(50L, operatingSystem.getCycleDurationMillis() / 2));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void checkPendingArrivals(long currentCycle) {
        synchronized (pendingProcesses) {
            for (int i = pendingProcesses.size() - 1; i >= 0; i--) {
                PendingProcess pending = pendingProcesses.get(i);
                if (currentCycle >= pending.arrivalCycle) {
                    operatingSystem.moveToReady(pending.pcb);
                    pendingProcesses.remove(i);
                }
            }
        }
    }

    private void startSimulation() {
        int value = speedSlider.getValue();
        operatingSystem.setCycleDurationMillis(value);
        ioHandler.setCycleDurationMillis(value);
        ensureIoThread();
        startArrivalChecker();
        operatingSystem.startSystemClock();
        updateSimulationControls();
    }

    private void pauseSimulation() {
        operatingSystem.stopSystemClock();
        updateSimulationControls();
    }

    private void restartSimulation() {
        operatingSystem.stopSystemClock();
        shutdownIoHandler();
        shutdownArrivalChecker();
        synchronized (pendingProcesses) {
            pendingProcesses = new ArrayList<>();
        }
        String selectedLabel = (String) policySelector.getSelectedItem();
        int rrValue = ((Number) RRQuantumSpinner.getValue()).intValue();
        int[] feedbackValues = collectFeedbackQuanta();
        int maxMemoryValue = ((Number) maxMemorySpinner.getValue()).intValue();
        initializeSimulationComponents();
        int speedValue = speedSlider.getValue();
        operatingSystem.setCycleDurationMillis(speedValue);
        ioHandler.setCycleDurationMillis(speedValue);
        updateSpeedLabel(speedValue);
        RRQuantumSpinner.setValue(rrValue);
        level0Spinner.setValue(feedbackValues[0]);
        level1Spinner.setValue(feedbackValues[1]);
        level2Spinner.setValue(feedbackValues[2]);
        level3Spinner.setValue(feedbackValues[3]);
        maxMemorySpinner.setValue(maxMemoryValue);
        operatingSystem.setMaxProcessesInMemory(maxMemoryValue);
        String targetLabel = selectedLabel != null ? selectedLabel : POLICY_OPTIONS[0];
        internalPolicyUpdate = true;
        policySelector.setSelectedItem(targetLabel);
        internalPolicyUpdate = false;
        PolicyType policy = resolvePolicy(targetLabel);
        updatePolicyControls(policy);
        applyPolicySelection(policy);
        updateSimulationControls();
    }

    private void shutdownIoHandler() {
        if (ioHandler != null) {
            ioHandler.stop();
        }
        if (ioThread != null && ioThread.isAlive()) {
            try {
                ioThread.join(1000L);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            if (ioThread.isAlive()) {
                ioThread.interrupt();
                try {
                    ioThread.join(500L);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            ioThread = null;
        }
    }

    private void shutdownArrivalChecker() {
        if (arrivalCheckerThread != null && arrivalCheckerThread.isAlive()) {
            arrivalCheckerThread.interrupt();
            try {
                arrivalCheckerThread.join(500L);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            arrivalCheckerThread = null;
        }
    }

    private void updateSimulationControls() {
        boolean running = operatingSystem != null && operatingSystem.isClockRunning();
        StartBtn.setEnabled(!running);
        pauseBtn.setEnabled(running);
        restartBtn.setEnabled(true);
    }

    private void createProcess() {
        try {
            String processName = processNameField.getText().trim();
            if (processName.isEmpty()) {
                processName = "P-" + System.currentTimeMillis();
            }
            int arrivalCycle = ((Number) arrivalSpinner.getValue()).intValue();
            int totalInstructions = ((Number) instructionSpinner.getValue()).intValue();
            boolean isIOBound = "I/O-bound".equals(processType.getSelectedItem());
            int ioCycle = ((Number) ioCycleSpinner.getValue()).intValue();
            int ioDuration = ((Number) ioDurationSpinner.getValue()).intValue();
            ProcessControlBlock pcb = new ProcessControlBlock(processName);
            pcb.setTotalInstructions(totalInstructions);
            pcb.setIOBound(isIOBound);
            if (isIOBound) {
                pcb.setIoExceptionCycle(ioCycle);
                pcb.setIoDuration(ioDuration);
            } else {
                pcb.setIoExceptionCycle(-1);
                pcb.setIoDuration(0);
            }
            long currentCycle = operatingSystem.getGlobalClockCycle();
            if (arrivalCycle <= currentCycle) {
                operatingSystem.moveToReady(pcb);
            } else {
                synchronized (pendingProcesses) {
                    pendingProcesses.add(new PendingProcess(pcb, arrivalCycle));
                }
            }
            processNameField.setText("");
            String arrivalMessage = arrivalCycle <= currentCycle
                ? "Agregado inmediatamente a la cola de listos"
                : "Programado para arribar en el ciclo " + arrivalCycle;
            JOptionPane.showMessageDialog(this,
                "Proceso creado exitosamente:\n" +
                "Nombre: " + pcb.getProcessName() + "\n" +
                "PID: " + pcb.getProcessId() + "\n" +
                "Instrucciones: " + totalInstructions + "\n" +
                arrivalMessage,
                "Proceso Creado",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al crear proceso", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void create20RandomProcesses() {
        try {
            long currentCycle = operatingSystem.getGlobalClockCycle();
            int processesCreated = 0;
            int processesScheduled = 0;
            for (int i = 0; i < 20; i++) {
                ProcessControlBlock pcb = processGenerator.generateRandomProcess();
                int arrivalCycle = processGenerator.generateRandomArrivalCycle(
                    (int) currentCycle,
                    (int) currentCycle + 50
                );
                if (arrivalCycle <= currentCycle) {
                    operatingSystem.moveToReady(pcb);
                    processesCreated++;
                } else {
                    synchronized (pendingProcesses) {
                        pendingProcesses.add(new PendingProcess(pcb, arrivalCycle));
                    }
                    processesScheduled++;
                }
            }
            JOptionPane.showMessageDialog(this,
                "20 procesos aleatorios creados exitosamente:\n" +
                "- Agregados inmediatamente: " + processesCreated + "\n" +
                "- Programados para arribar: " + processesScheduled + "\n" +
                "Rango de arribo: ciclo " + currentCycle + " a " + (currentCycle + 50),
                "Procesos Creados",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al crear procesos aleatorios: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        Simulacion = new javax.swing.JPanel();
        ControlsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        policySelector = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        RRQuantumSpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        level0Spinner = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        level1Spinner = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        level2Spinner = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        level3Spinner = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        speedSlider = new javax.swing.JSlider();
        msLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        maxMemorySpinner = new javax.swing.JSpinner();
        pauseBtn = new javax.swing.JButton();
        StartBtn = new javax.swing.JButton();
        restartBtn = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        processNameField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        arrivalSpinner = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        instructionSpinner = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        ioCycleSpinner = new javax.swing.JSpinner();
        createProcessBtn = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        processType = new javax.swing.JComboBox<>();
        ioDurationSpinner = new javax.swing.JSpinner();
        Create20ProcessBtn = new javax.swing.JButton();
        CpuPanel = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        currentCycleLabel = new javax.swing.JLabel();
        modeLabel = new javax.swing.JLabel();
        totalLabel = new javax.swing.JLabel();
        marLabel = new javax.swing.JLabel();
        pcLabel = new javax.swing.JLabel();
        pidLabel = new javax.swing.JLabel();
        processLabel = new javax.swing.JLabel();
        QueuesPanel = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        listosList = new javax.swing.JList<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        bloqList = new javax.swing.JList<>();
        jScrollPane8 = new javax.swing.JScrollPane();
        finishedList = new javax.swing.JList<>();
        jScrollPane9 = new javax.swing.JScrollPane();
        susList = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        saveFile = new javax.swing.JButton();
        loadFile = new javax.swing.JButton();
        Logs = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        logList = new javax.swing.JList<>();
        Charts = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Simulacion.setBackground(new java.awt.Color(53, 73, 133));
        Simulacion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ControlsPanel.setBackground(new java.awt.Color(53, 73, 133));
        ControlsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Controles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        ControlsPanel.setForeground(new java.awt.Color(255, 255, 255));
        ControlsPanel.setOpaque(false);
        ControlsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Simulador de CPU");
        ControlsPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Planificador:");
        ControlsPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 110, 20));

        policySelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FCFS", "Round Robin", "SPN", "SRTF", "HRRN", "Feedback" }));
        policySelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                policySelectorActionPerformed(evt);
            }
        });
        ControlsPanel.add(policySelector, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 690, -1));

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Quantum:");
        ControlsPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 50, 60, 20));
        ControlsPanel.add(RRQuantumSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 50, 60, -1));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Feedback:");
        ControlsPanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Nivel 0:");
        ControlsPanel.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 80, 60, 20));
        ControlsPanel.add(level0Spinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 60, -1));

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Nivel 1:");
        ControlsPanel.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 80, 60, 20));
        ControlsPanel.add(level1Spinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, 60, -1));

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Nivel 2:");
        ControlsPanel.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 80, 60, 20));
        ControlsPanel.add(level2Spinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 80, 60, -1));

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Nivel 3:");
        ControlsPanel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 80, 60, 20));
        ControlsPanel.add(level3Spinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 80, 60, -1));

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Velocidad (ms):");
        ControlsPanel.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 90, 20));
        ControlsPanel.add(speedSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 110, 670, -1));

        msLabel.setForeground(new java.awt.Color(255, 255, 255));
        msLabel.setText("--         ms");
        ControlsPanel.add(msLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 110, 100, 20));

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Procesos en memoria:");
        ControlsPanel.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, -1, -1));
        ControlsPanel.add(maxMemorySpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 140, -1, -1));

        pauseBtn.setText("Pausar");
        pauseBtn.setPreferredSize(new java.awt.Dimension(73, 23));
        pauseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseBtnActionPerformed(evt);
            }
        });
        ControlsPanel.add(pauseBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 180, -1, -1));

        StartBtn.setText("Iniciar");
        StartBtn.setPreferredSize(new java.awt.Dimension(73, 23));
        StartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartBtnActionPerformed(evt);
            }
        });
        ControlsPanel.add(StartBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 180, -1, -1));

        restartBtn.setText("Reiniciar");
        restartBtn.setMaximumSize(new java.awt.Dimension(72, 23));
        restartBtn.setMinimumSize(new java.awt.Dimension(72, 23));
        restartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restartBtnActionPerformed(evt);
            }
        });
        ControlsPanel.add(restartBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 180, -1, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Crear Proceso");
        ControlsPanel.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 130, 30));

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Nombre:");
        ControlsPanel.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 50, 20));

        processNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processNameFieldActionPerformed(evt);
            }
        });
        ControlsPanel.add(processNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 240, 490, -1));

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Arribo (ciclo):");
        ControlsPanel.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 90, 20));
        ControlsPanel.add(arrivalSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 270, 80, -1));

        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Instrucciones:");
        ControlsPanel.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 270, 100, 20));
        ControlsPanel.add(instructionSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 270, 80, -1));

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Ciclo I/O:");
        ControlsPanel.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 270, 70, 20));
        ControlsPanel.add(ioCycleSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 270, 90, -1));

        createProcessBtn.setText("Crear Proceso");
        createProcessBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createProcessBtnActionPerformed(evt);
            }
        });
        ControlsPanel.add(createProcessBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, 140, -1));

        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Tipo:");
        ControlsPanel.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 270, 80, 20));

        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Duracion I/O:");
        ControlsPanel.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 300, 80, 20));

        processType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        processType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processTypeActionPerformed(evt);
            }
        });
        ControlsPanel.add(processType, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 270, 130, -1));
        ControlsPanel.add(ioDurationSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 300, 130, -1));

        Create20ProcessBtn.setText("Crear 20 Procesos");
        Create20ProcessBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Create20ProcessBtnActionPerformed(evt);
            }
        });
        ControlsPanel.add(Create20ProcessBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 310, -1, -1));

        Simulacion.add(ControlsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1030, 350));

        CpuPanel.setBackground(new java.awt.Color(53, 73, 133));
        CpuPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CPU", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        CpuPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Proceso:");
        CpuPanel.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("ID:");
        CpuPanel.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("PC:");
        CpuPanel.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("MAR:");
        CpuPanel.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Total:");
        CpuPanel.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, -1, -1));

        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Ciclo actual:");
        CpuPanel.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, -1, -1));

        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Modo:");
        CpuPanel.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, -1));

        currentCycleLabel.setForeground(new java.awt.Color(255, 255, 255));
        currentCycleLabel.setText("0");
        CpuPanel.add(currentCycleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 180, 60, -1));

        modeLabel.setForeground(new java.awt.Color(255, 255, 255));
        modeLabel.setText("OS");
        CpuPanel.add(modeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 210, 60, -1));

        totalLabel.setForeground(new java.awt.Color(255, 255, 255));
        totalLabel.setText("--");
        CpuPanel.add(totalLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 150, 60, -1));

        marLabel.setForeground(new java.awt.Color(255, 255, 255));
        marLabel.setText("--");
        CpuPanel.add(marLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 120, 60, -1));

        pcLabel.setForeground(new java.awt.Color(255, 255, 255));
        pcLabel.setText("--");
        CpuPanel.add(pcLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, 60, -1));

        pidLabel.setForeground(new java.awt.Color(255, 255, 255));
        pidLabel.setText("--");
        CpuPanel.add(pidLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 60, -1));

        processLabel.setForeground(new java.awt.Color(255, 255, 255));
        processLabel.setText("N/A");
        CpuPanel.add(processLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 60, -1));

        Simulacion.add(CpuPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 370, 180, 240));

        QueuesPanel.setBackground(new java.awt.Color(53, 73, 133));
        QueuesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Colas de Procesos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        QueuesPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("Listos");
        QueuesPanel.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 110, -1));

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("Suspendidos");
        QueuesPanel.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 30, 110, -1));

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("Terminados");
        QueuesPanel.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, 110, -1));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("Bloqueados");
        QueuesPanel.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, 110, -1));

        listosList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane6.setViewportView(listosList);

        QueuesPanel.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 100, 160));

        bloqList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane7.setViewportView(bloqList);

        QueuesPanel.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, 100, 160));

        finishedList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane8.setViewportView(finishedList);

        QueuesPanel.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 100, 160));

        susList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane9.setViewportView(susList);

        QueuesPanel.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 60, 100, 160));

        Simulacion.add(QueuesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 500, 230));

        jPanel1.setBackground(new java.awt.Color(53, 73, 133));

        saveFile.setText("Guardar");
        saveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFileActionPerformed(evt);
            }
        });

        loadFile.setText("Cargar");
        loadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(saveFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(loadFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(126, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(saveFile)
                .addGap(48, 48, 48)
                .addComponent(loadFile)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        Simulacion.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 380, 310, 220));

        jTabbedPane1.addTab("Simulacion", Simulacion);

        Logs.setBackground(new java.awt.Color(53, 73, 133));
        Logs.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(logList);

        Logs.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 970, 540));

        jTabbedPane1.addTab("Configuracion", Logs);

        Charts.setBackground(new java.awt.Color(53, 73, 133));
        Charts.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jTabbedPane1.addTab("Graficos", Charts);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createProcessBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createProcessBtnActionPerformed
        createProcess();
    }//GEN-LAST:event_createProcessBtnActionPerformed

    private void restartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restartBtnActionPerformed
        restartSimulation();
    }//GEN-LAST:event_restartBtnActionPerformed

    private void loadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_loadFileActionPerformed

    private void policySelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_policySelectorActionPerformed
        if (internalPolicyUpdate) {
            return;
        }
        Object selectedItem = policySelector.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        PolicyType selectedPolicy = resolvePolicy(selectedItem.toString());
        updatePolicyControls(selectedPolicy);
        applyPolicySelection(selectedPolicy);
    }//GEN-LAST:event_policySelectorActionPerformed

    private void StartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartBtnActionPerformed
        startSimulation();
    }//GEN-LAST:event_StartBtnActionPerformed

    private void pauseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseBtnActionPerformed
        pauseSimulation();
    }//GEN-LAST:event_pauseBtnActionPerformed

    private void processNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_processNameFieldActionPerformed

    private void processTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processTypeActionPerformed
        updateProcessTypeFields();
    }//GEN-LAST:event_processTypeActionPerformed

    private void Create20ProcessBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Create20ProcessBtnActionPerformed
        create20RandomProcesses();
    }//GEN-LAST:event_Create20ProcessBtnActionPerformed

    private void saveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveFileActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewMainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Charts;
    private javax.swing.JPanel ControlsPanel;
    private javax.swing.JPanel CpuPanel;
    private javax.swing.JButton Create20ProcessBtn;
    private javax.swing.JPanel Logs;
    private javax.swing.JPanel QueuesPanel;
    private javax.swing.JSpinner RRQuantumSpinner;
    private javax.swing.JPanel Simulacion;
    private javax.swing.JButton StartBtn;
    private javax.swing.JSpinner arrivalSpinner;
    private javax.swing.JList<String> bloqList;
    private javax.swing.JButton createProcessBtn;
    private javax.swing.JLabel currentCycleLabel;
    private javax.swing.JList<String> finishedList;
    private javax.swing.JSpinner instructionSpinner;
    private javax.swing.JSpinner ioCycleSpinner;
    private javax.swing.JSpinner ioDurationSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JSpinner level0Spinner;
    private javax.swing.JSpinner level1Spinner;
    private javax.swing.JSpinner level2Spinner;
    private javax.swing.JSpinner level3Spinner;
    private javax.swing.JList<String> listosList;
    private javax.swing.JButton loadFile;
    private javax.swing.JList<String> logList;
    private javax.swing.JLabel marLabel;
    private javax.swing.JSpinner maxMemorySpinner;
    private javax.swing.JLabel modeLabel;
    private javax.swing.JLabel msLabel;
    private javax.swing.JButton pauseBtn;
    private javax.swing.JLabel pcLabel;
    private javax.swing.JLabel pidLabel;
    private javax.swing.JComboBox<String> policySelector;
    private javax.swing.JLabel processLabel;
    private javax.swing.JTextField processNameField;
    private javax.swing.JComboBox<String> processType;
    private javax.swing.JButton restartBtn;
    private javax.swing.JButton saveFile;
    private javax.swing.JSlider speedSlider;
    private javax.swing.JList<String> susList;
    private javax.swing.JLabel totalLabel;
    // End of variables declaration//GEN-END:variables
}
