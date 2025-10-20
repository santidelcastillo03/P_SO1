/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import scheduler.PolicyType;

/**
 * Panel superior con los controles principales del simulador.
 * Permite seleccionar el planificador y ejecutar acciones básicas.
 *
 * @author santiagodelcastillo
 */
public class ControlsPanel extends javax.swing.JPanel {

    private static final int MIN_SPEED = 10;
    private static final int MAX_SPEED = 1000;
    private static final String CPU_BOUND_OPTION = "CPU bound";
    private static final String IO_BOUND_OPTION = "I/O bound";
    private transient Consumer<PolicyType> policyListener;
    private transient LongConsumer speedListener;
    private transient IntConsumer quantumListener;
    private transient Consumer<ProcessFormData> processListener;
    private transient Consumer<String> scenarioListener;
    private boolean updating;

    /**
     * Construye el panel de controles que agrupa las acciones principales.
     */
    public ControlsPanel() {
        initComponents();
        planificadorComboBox.addActionListener(evt -> handlePolicySelection());
        velocidadSlider.addChangeListener(evt -> handleSpeedChange());
        quantumSpinner.addChangeListener(evt -> handleQuantumChange());
        tipoComboBox.addActionListener(evt -> updateIoInputs());
        crearProcesoButton.addActionListener(evt -> handleCreateProcess());
        cargarEscenarioButton.addActionListener(evt -> handleLoadScenario());
        setControlsState(PolicyType.FCFS, 100L, 4);
        updateIoInputs();
    }

    /**
     * Este método es invocado por el constructor para inicializar los componentes.
     * ADVERTENCIA: no modificar manualmente; el editor de formularios lo regenera.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Código generado">//GEN-BEGIN:initComponents
    private void initComponents() {

        tituloLabel = new javax.swing.JLabel();
        planificadorLabel = new javax.swing.JLabel();
        planificadorComboBox = new javax.swing.JComboBox<>();
        quantumLabel = new javax.swing.JLabel();
        quantumSpinner = new javax.swing.JSpinner();
        velocidadLabel = new javax.swing.JLabel();
        velocidadSlider = new javax.swing.JSlider();
        velocidadValorLabel = new javax.swing.JLabel();
        iniciarButton = new javax.swing.JButton();
        pausarButton = new javax.swing.JButton();
        reiniciarButton = new javax.swing.JButton();
        procesosLabel = new javax.swing.JLabel();
        escenarioLabel = new javax.swing.JLabel();
        escenarioComboBox = new javax.swing.JComboBox<>();
        cargarEscenarioButton = new javax.swing.JButton();
        nombreLabel = new javax.swing.JLabel();
        nombreTextField = new javax.swing.JTextField();
        arriboLabel = new javax.swing.JLabel();
        arriboSpinner = new javax.swing.JSpinner();
        instruccionesLabel = new javax.swing.JLabel();
        instruccionesSpinner = new javax.swing.JSpinner();
        tipoLabel = new javax.swing.JLabel();
        tipoComboBox = new javax.swing.JComboBox<>();
        ioCycleLabel = new javax.swing.JLabel();
        ioCycleSpinner = new javax.swing.JSpinner();
        ioDurationLabel = new javax.swing.JLabel();
        ioDurationSpinner = new javax.swing.JSpinner();
        crearProcesoButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Controles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, getFont().deriveFont(java.awt.Font.BOLD)));
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        tituloLabel.setFont(getFont().deriveFont(getFont().getStyle() | java.awt.Font.BOLD, 16f));
        tituloLabel.setText("Simulador de CPU");
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 0);
        add(tituloLabel, gridBagConstraints);

        planificadorLabel.setText("Planificador:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        add(planificadorLabel, gridBagConstraints);

        planificadorComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FCFS", "SRTF", "SPN", "HRRN", "Round Robin", "Feedback" }));
        planificadorComboBox.setPrototypeDisplayValue("Round Robin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(planificadorComboBox, gridBagConstraints);

        quantumLabel.setText("Quantum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(quantumLabel, gridBagConstraints);

        quantumSpinner.setModel(new SpinnerNumberModel(4, 1, 50, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(quantumSpinner, gridBagConstraints);

        velocidadLabel.setText("Velocidad (ms):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 6);
        add(velocidadLabel, gridBagConstraints);

        velocidadSlider.setMinimum(MIN_SPEED);
        velocidadSlider.setMaximum(MAX_SPEED);
        velocidadSlider.setValue(100);
        velocidadSlider.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 6);
        add(velocidadSlider, gridBagConstraints);

        velocidadValorLabel.setText("100 ms");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(velocidadValorLabel, gridBagConstraints);

        iniciarButton.setText("Iniciar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 6);
        add(iniciarButton, gridBagConstraints);

        pausarButton.setText("Pausar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 6);
        add(pausarButton, gridBagConstraints);

        reiniciarButton.setText("Reiniciar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(reiniciarButton, gridBagConstraints);

        procesosLabel.setFont(getFont().deriveFont(getFont().getStyle() | java.awt.Font.BOLD, 14f));
        procesosLabel.setText("Crear proceso");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 8, 0);
        add(procesosLabel, gridBagConstraints);

        escenarioLabel.setText("Escenario:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        add(escenarioLabel, gridBagConstraints);

        escenarioComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "FCFS", "SPN", "HRRN", "SRTF", "Round Robin", "Feedback" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(escenarioComboBox, gridBagConstraints);

        cargarEscenarioButton.setText("Cargar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(cargarEscenarioButton, gridBagConstraints);

        nombreLabel.setText("Nombre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        add(nombreLabel, gridBagConstraints);

        nombreTextField.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(nombreTextField, gridBagConstraints);

        arriboLabel.setText("Arribo (ciclo):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 6);
        add(arriboLabel, gridBagConstraints);

        arriboSpinner.setModel(new SpinnerNumberModel(0, 0, 2000, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 12);
        add(arriboSpinner, gridBagConstraints);

        instruccionesLabel.setText("Instrucciones:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 6);
        add(instruccionesLabel, gridBagConstraints);

        instruccionesSpinner.setModel(new SpinnerNumberModel(10, 1, 500, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 12);
        add(instruccionesSpinner, gridBagConstraints);

        tipoLabel.setText("Tipo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 6);
        add(tipoLabel, gridBagConstraints);

        tipoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { CPU_BOUND_OPTION, IO_BOUND_OPTION }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        add(tipoComboBox, gridBagConstraints);

        ioCycleLabel.setText("Ciclo I/O:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 6);
        add(ioCycleLabel, gridBagConstraints);

        ioCycleSpinner.setModel(new SpinnerNumberModel(3, 0, 500, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 12);
        add(ioCycleSpinner, gridBagConstraints);

        ioDurationLabel.setText("Duración I/O:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 6);
        add(ioDurationLabel, gridBagConstraints);

        ioDurationSpinner.setModel(new SpinnerNumberModel(2, 1, 200, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        add(ioDurationSpinner, gridBagConstraints);

        crearProcesoButton.setText("Crear proceso");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(crearProcesoButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    public void setPolicyChangeListener(Consumer<PolicyType> listener) {
        this.policyListener = listener;
    }

    public void setSpeedChangeListener(LongConsumer listener) {
        this.speedListener = listener;
    }

    public void setQuantumChangeListener(IntConsumer listener) {
        this.quantumListener = listener;
    }

    public void setProcessCreationListener(Consumer<ProcessFormData> listener) {
        this.processListener = listener;
    }

    public void setScenarioLoadListener(Consumer<String> listener) {
        this.scenarioListener = listener;
    }

    public void setControlsState(PolicyType policyType, long cycleDurationMs, int quantum) {
        PolicyType resolvedPolicy = policyType != null ? policyType : PolicyType.FCFS;
        long clampedDuration = Math.max(MIN_SPEED, Math.min(MAX_SPEED, cycleDurationMs));
        int normalizedQuantum = Math.max(1, Math.min(50, quantum));
        updating = true;
        planificadorComboBox.setSelectedIndex(policyToIndex(resolvedPolicy));
        velocidadSlider.setValue((int) clampedDuration);
        velocidadValorLabel.setText(formatDuration(clampedDuration));
        quantumSpinner.setValue(normalizedQuantum);
        updateQuantumControls(resolvedPolicy);
        updating = false;
    }

    private void handlePolicySelection() {
        if (updating) {
            return;
        }
        String selected = Objects.toString(planificadorComboBox.getSelectedItem(), "").trim();
        PolicyType policy = selectionToPolicy(selected);
        updateQuantumControls(policy);
        if (policyListener != null) {
            policyListener.accept(policy);
        }
    }

    private void handleSpeedChange() {
        long duration = sliderToDuration(velocidadSlider.getValue());
        velocidadValorLabel.setText(formatDuration(duration));
        if (updating || velocidadSlider.getValueIsAdjusting()) {
            return;
        }
        if (speedListener != null) {
            speedListener.accept(duration);
        }
    }

    private void handleQuantumChange() {
        if (updating || !quantumSpinner.isEnabled()) {
            return;
        }
        int quantum = ((Number) quantumSpinner.getValue()).intValue();
        if (quantumListener != null) {
            quantumListener.accept(quantum);
        }
    }

    private void handleCreateProcess() {
        String nombre = nombreTextField.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Validación", JOptionPane.WARNING_MESSAGE);
            nombreTextField.requestFocusInWindow();
            return;
        }
        int instrucciones = ((Number) instruccionesSpinner.getValue()).intValue();
        boolean ioBound = IO_BOUND_OPTION.equals(tipoComboBox.getSelectedItem());
        int ioCycle = ((Number) ioCycleSpinner.getValue()).intValue();
        int ioDuration = ((Number) ioDurationSpinner.getValue()).intValue();
        int arribo = ((Number) arriboSpinner.getValue()).intValue();
        if (ioBound && ioCycle >= instrucciones) {
            JOptionPane.showMessageDialog(this, "El ciclo de I/O debe ser menor al total de instrucciones", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!ioBound) {
            ioCycle = -1;
            ioDuration = 0;
        }
        if (processListener != null) {
            processListener.accept(new ProcessFormData(nombre, arribo, instrucciones, ioBound, ioCycle, ioDuration));
        }
        clearProcessForm();
    }

    private void handleLoadScenario() {
        if (scenarioListener == null) {
            return;
        }
        Object selection = escenarioComboBox.getSelectedItem();
        if (selection == null) {
            return;
        }
        String scenario = selection.toString();
        if ("Seleccionar".equalsIgnoreCase(scenario)) {
            return;
        }
        scenarioListener.accept(scenario);
    }

    private int policyToIndex(PolicyType policy) {
        return switch (policy) {
            case FCFS -> 0;
            case SRT -> 1;
            case SPN -> 2;
            case HRRN -> 3;
            case ROUND_ROBIN -> 4;
            case FEEDBACK -> 5;
        };
    }

    private PolicyType selectionToPolicy(String value) {
        return switch (value) {
            case "SRTF" -> PolicyType.SRT;
            case "SPN" -> PolicyType.SPN;
            case "HRRN" -> PolicyType.HRRN;
            case "Round Robin" -> PolicyType.ROUND_ROBIN;
            case "Feedback" -> PolicyType.FEEDBACK;
            default -> PolicyType.FCFS;
        };
    }

    private long sliderToDuration(int sliderValue) {
        return Math.max(MIN_SPEED, Math.min(MAX_SPEED, sliderValue));
    }

    private String formatDuration(long duration) {
        return duration + " ms";
    }

    private void updateQuantumControls(PolicyType policy) {
        boolean enabled = policy == PolicyType.ROUND_ROBIN;
        quantumLabel.setEnabled(enabled);
        quantumSpinner.setEnabled(enabled);
    }

    private void updateIoInputs() {
        boolean ioSelected = IO_BOUND_OPTION.equals(tipoComboBox.getSelectedItem());
        ioCycleLabel.setEnabled(ioSelected);
        ioCycleSpinner.setEnabled(ioSelected);
        ioDurationLabel.setEnabled(ioSelected);
        ioDurationSpinner.setEnabled(ioSelected);
    }

    private void clearProcessForm() {
        nombreTextField.setText("");
        arriboSpinner.setValue(0);
        instruccionesSpinner.setValue(10);
        tipoComboBox.setSelectedItem(CPU_BOUND_OPTION);
        ioCycleSpinner.setValue(3);
        ioDurationSpinner.setValue(2);
        updateIoInputs();
    }

    public static final class ProcessFormData {
        public final String nombre;
        public final int arribo;
        public final int instrucciones;
        public final boolean ioBound;
        public final int ioCycle;
        public final int ioDuration;

        public ProcessFormData(String nombre,
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
    private javax.swing.JLabel arriboLabel;
    private javax.swing.JSpinner arriboSpinner;
    private javax.swing.JButton cargarEscenarioButton;
    private javax.swing.JButton crearProcesoButton;
    private javax.swing.JComboBox<String> escenarioComboBox;
    private javax.swing.JLabel escenarioLabel;
    private javax.swing.JButton iniciarButton;
    private javax.swing.JButton pausarButton;
    private javax.swing.JComboBox<String> planificadorComboBox;
    private javax.swing.JLabel planificadorLabel;
    private javax.swing.JLabel procesosLabel;
    private javax.swing.JLabel quantumLabel;
    private javax.swing.JSpinner quantumSpinner;
    private javax.swing.JButton reiniciarButton;
    private javax.swing.JLabel tituloLabel;
    private javax.swing.JLabel instruccionesLabel;
    private javax.swing.JSpinner instruccionesSpinner;
    private javax.swing.JLabel ioCycleLabel;
    private javax.swing.JSpinner ioCycleSpinner;
    private javax.swing.JLabel ioDurationLabel;
    private javax.swing.JSpinner ioDurationSpinner;
    private javax.swing.JLabel nombreLabel;
    private javax.swing.JTextField nombreTextField;
    private javax.swing.JComboBox<String> tipoComboBox;
    private javax.swing.JLabel tipoLabel;
    private javax.swing.JLabel velocidadLabel;
    private javax.swing.JSlider velocidadSlider;
    private javax.swing.JLabel velocidadValorLabel;
    // Fin de la declaración de variables//GEN-END:variables
}
