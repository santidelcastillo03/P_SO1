/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui;

import java.util.Arrays;
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

    private static final int MIN_SPEED = 0;
    private static final int MAX_SPEED = 1000;
    private static final int[] DEFAULT_FEEDBACK_QUANTA = {1, 2, 3, 4};
    private static final int MIN_MEMORY_LIMIT = 1;
    private static final int MAX_MEMORY_LIMIT = 100;
    private static final int DEFAULT_MEMORY_LIMIT = 25;
    private static final String CPU_BOUND_OPTION = "CPU bound";
    private static final String IO_BOUND_OPTION = "I/O bound";
    private transient Consumer<PolicyType> policyListener;
    private transient LongConsumer speedListener;
    private transient IntConsumer quantumListener;
    private transient Consumer<int[]> feedbackQuantumListener;
    private transient Consumer<ProcessFormData> processListener;
    private transient IntConsumer maxMemoryListener;
    private transient Runnable randomProcessesListener;
    private transient Runnable startListener;
    private transient Runnable pauseListener;
    private transient Runnable resetListener;
    private boolean updating;
    private boolean simulationRunning;
    private boolean simulationStarted;

    /**
     * Construye el panel de controles que agrupa las acciones principales.
     */
    public ControlsPanel() {
        initComponents();
        planificadorComboBox.addActionListener(evt -> handlePolicySelection());
        velocidadSlider.addChangeListener(evt -> handleSpeedChange());
        quantumSpinner.addChangeListener(evt -> handleQuantumChange());
        feedbackQuantumSpinner0.addChangeListener(evt -> handleFeedbackQuantumChange());
        feedbackQuantumSpinner1.addChangeListener(evt -> handleFeedbackQuantumChange());
        feedbackQuantumSpinner2.addChangeListener(evt -> handleFeedbackQuantumChange());
        feedbackQuantumSpinner3.addChangeListener(evt -> handleFeedbackQuantumChange());
        tipoComboBox.addActionListener(evt -> updateIoInputs());
        crearProcesoButton.addActionListener(evt -> handleCreateProcess());
        agregarAleatoriosButton.addActionListener(evt -> handleRandomProcessesRequest());
        memoriaSpinner.addChangeListener(evt -> handleMaxMemoryChange());
        iniciarButton.addActionListener(evt -> handleStartClick());
        pausarButton.addActionListener(evt -> handlePauseClick());
        reiniciarButton.addActionListener(evt -> handleResetClick());
        setControlsState(PolicyType.FCFS, 100L, 4);
        updateIoInputs();
        setSimulationState(false, false);
    }

    /**
     * Este método es invocado por el constructor para inicializar los componentes.
     * ADVERTENCIA: no modificar manualmente; el editor de formularios lo regenera.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tituloLabel = new javax.swing.JLabel();
        planificadorLabel = new javax.swing.JLabel();
        planificadorComboBox = new javax.swing.JComboBox<>();
        quantumLabel = new javax.swing.JLabel();
        quantumSpinner = new javax.swing.JSpinner();
        feedbackLabel = new javax.swing.JLabel();
        feedbackPanel = new javax.swing.JPanel();
        feedbackNivel0Label = new javax.swing.JLabel();
        feedbackQuantumSpinner0 = new javax.swing.JSpinner();
        feedbackNivel1Label = new javax.swing.JLabel();
        feedbackQuantumSpinner1 = new javax.swing.JSpinner();
        feedbackNivel2Label = new javax.swing.JLabel();
        feedbackQuantumSpinner2 = new javax.swing.JSpinner();
        feedbackNivel3Label = new javax.swing.JLabel();
        feedbackQuantumSpinner3 = new javax.swing.JSpinner();
        velocidadLabel = new javax.swing.JLabel();
        velocidadSlider = new javax.swing.JSlider();
        velocidadSlider.setMinimum(MIN_SPEED);
        velocidadSlider.setMaximum(MAX_SPEED);
        velocidadSlider.setValue(100);
        velocidadValorLabel = new javax.swing.JLabel();
        memoriaLabel = new javax.swing.JLabel();
        memoriaSpinner = new javax.swing.JSpinner();
        iniciarButton = new javax.swing.JButton();
        pausarButton = new javax.swing.JButton();
        reiniciarButton = new javax.swing.JButton();
        procesosLabel = new javax.swing.JLabel();
        agregarAleatoriosButton = new javax.swing.JButton();
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

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Controles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        tituloLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        tituloLabel.setText("Simulador de CPU");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        add(tituloLabel, gridBagConstraints);

        planificadorLabel.setText("Planificador:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        add(planificadorLabel, gridBagConstraints);

        planificadorComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FCFS", "SRTF", "SPN", "HRRN", "Round Robin", "Feedback" }));
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(quantumSpinner, gridBagConstraints);

        feedbackLabel.setText("Feedback:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 6);
        add(feedbackLabel, gridBagConstraints);

        feedbackPanel.setOpaque(false);
        feedbackPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));

        feedbackNivel0Label.setText("Nivel 0:");
        feedbackPanel.add(feedbackNivel0Label);
        feedbackPanel.add(feedbackQuantumSpinner0);

        feedbackNivel1Label.setText("Nivel 1:");
        feedbackPanel.add(feedbackNivel1Label);
        feedbackPanel.add(feedbackQuantumSpinner1);

        feedbackNivel2Label.setText("Nivel 2:");
        feedbackPanel.add(feedbackNivel2Label);
        feedbackPanel.add(feedbackQuantumSpinner2);

        feedbackNivel3Label.setText("Nivel 3:");
        feedbackPanel.add(feedbackNivel3Label);
        feedbackPanel.add(feedbackQuantumSpinner3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(feedbackPanel, gridBagConstraints);

        velocidadLabel.setText("Velocidad (ms):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 6);
        add(velocidadLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 6);
        add(velocidadSlider, gridBagConstraints);

        velocidadValorLabel.setText("100 ms");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(velocidadValorLabel, gridBagConstraints);

        memoriaLabel.setText("Procesos en memoria:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 6);
        add(memoriaLabel, gridBagConstraints);

        memoriaSpinner.setModel(new SpinnerNumberModel(DEFAULT_MEMORY_LIMIT, MIN_MEMORY_LIMIT, MAX_MEMORY_LIMIT, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(memoriaSpinner, gridBagConstraints);

        iniciarButton.setText("Iniciar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 6);
        add(iniciarButton, gridBagConstraints);

        pausarButton.setText("Pausar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 6);
        add(pausarButton, gridBagConstraints);

        reiniciarButton.setText("Reiniciar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        add(reiniciarButton, gridBagConstraints);

        procesosLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        procesosLabel.setText("Crear proceso");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 4, 0);
        add(procesosLabel, gridBagConstraints);

        agregarAleatoriosButton.setText("Agregar 20 aleatorios");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(agregarAleatoriosButton, gridBagConstraints);

        nombreLabel.setText("Nombre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        add(nombreLabel, gridBagConstraints);

        nombreTextField.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(nombreTextField, gridBagConstraints);

        arriboLabel.setText("Arribo (ciclo):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 6);
        add(arriboLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 12);
        add(arriboSpinner, gridBagConstraints);

        instruccionesLabel.setText("Instrucciones:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 6);
        add(instruccionesLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 12);
        add(instruccionesSpinner, gridBagConstraints);

        tipoLabel.setText("Tipo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 6);
        add(tipoLabel, gridBagConstraints);

        tipoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CPU bound", "I/O bound" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(tipoComboBox, gridBagConstraints);

        ioCycleLabel.setText("Ciclo I/O:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 6);
        add(ioCycleLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 12);
        add(ioCycleSpinner, gridBagConstraints);

        ioDurationLabel.setText("Duración I/O:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 6);
        add(ioDurationLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(ioDurationSpinner, gridBagConstraints);

        crearProcesoButton.setText("Crear proceso");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
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

    public void setFeedbackQuantumListener(Consumer<int[]> listener) {
        this.feedbackQuantumListener = listener;
    }

    public void setProcessCreationListener(Consumer<ProcessFormData> listener) {
        this.processListener = listener;
    }

    /**
     * Registra el callback que actualiza el límite máximo de procesos en memoria.
     * @param listener acción a ejecutar al cambiar el spinner de memoria
     */
    public void setMaxMemoryListener(IntConsumer listener) {
        this.maxMemoryListener = listener;
    }

    /**
     * Registra el callback que genera procesos aleatorios en lote.
     * @param listener acción a ejecutar al presionar el botón de procesos aleatorios
     */
    public void setRandomProcessesListener(Runnable listener) {
        this.randomProcessesListener = listener;
    }

    /**
     * Registra el callback que se ejecuta cuando se solicita iniciar la simulación.
     * @param listener acción a ejecutar al presionar el botón Iniciar
     */
    public void setStartListener(Runnable listener) {
        this.startListener = listener;
    }

    /**
     * Registra el callback que se ejecuta cuando se solicita pausar la simulación.
     * @param listener acción a ejecutar al presionar el botón Pausar
     */
    public void setPauseListener(Runnable listener) {
        this.pauseListener = listener;
    }

    /**
     * Registra el callback que se ejecuta cuando se solicita reiniciar la simulación.
     * @param listener acción a ejecutar al presionar el botón Reiniciar
     */
    public void setResetListener(Runnable listener) {
        this.resetListener = listener;
    }

    /**
     * Actualiza el estado visual de los botones de simulación.
     * @param running indica si el reloj del sistema está activo
     * @param started señala si la simulación se ha inicializado al menos una vez
     */
    public void setSimulationState(boolean running, boolean started) {
        this.simulationRunning = running;
        this.simulationStarted = started || running;
        updateSimulationButtons();
    }

    /**
     * Devuelve la política actualmente seleccionada en el combo.
     * @return política elegida por el usuario
     */
    public PolicyType getSelectedPolicyType() {
        String selected = Objects.toString(planificadorComboBox.getSelectedItem(), "").trim();
        return selectionToPolicy(selected);
    }

    /**
     * Devuelve la velocidad (ms por ciclo) configurada en el deslizador.
     * @return duración de ciclo elegida
     */
    public long getSelectedSpeedMillis() {
        return sliderToDuration(velocidadSlider.getValue());
    }

    /**
     * Devuelve el quantum configurado para Round Robin.
     * @return valor numérico del spinner de quantum
     */
    public int getSelectedQuantumValue() {
        return ((Number) quantumSpinner.getValue()).intValue();
    }

    /**
     * Devuelve los quantums configurados para los niveles de Feedback.
     * @return arreglo con los quantums actuales
     */
    public int[] getSelectedFeedbackQuanta() {
        return readFeedbackQuanta();
    }

    /**
     * Devuelve el límite de procesos residiendo simultáneamente en memoria.
     * @return valor actual del spinner de memoria
     */
    public int getSelectedMaxMemoryValue() {
        return ((Number) memoriaSpinner.getValue()).intValue();
    }

    public void setControlsState(PolicyType policyType, long cycleDurationMs, int quantum) {
        setControlsState(policyType, cycleDurationMs, quantum, null, getSelectedMaxMemoryValue());
    }

    public void setControlsState(PolicyType policyType, long cycleDurationMs, int quantum, int[] feedbackQuanta) {
        setControlsState(policyType, cycleDurationMs, quantum, feedbackQuanta, getSelectedMaxMemoryValue());
    }

    /**
     * Sincroniza los controles visuales con los valores del sistema operativo activo.
     * @param policyType política de planificación a mostrar
     * @param cycleDurationMs duración del ciclo en milisegundos
     * @param quantum quantum utilizado en Round Robin
     * @param feedbackQuanta arreglo de quantums por nivel para Feedback
     * @param maxMemory límite máximo de procesos en memoria principal
     */
    public void setControlsState(PolicyType policyType,
                                 long cycleDurationMs,
                                 int quantum,
                                 int[] feedbackQuanta,
                                 int maxMemory) {
        PolicyType resolvedPolicy = policyType != null ? policyType : PolicyType.FCFS;
        long clampedDuration = Math.max(MIN_SPEED, Math.min(MAX_SPEED, cycleDurationMs));
        int normalizedQuantum = Math.max(1, Math.min(50, quantum));
        int[] normalizedFeedbackQuanta = normalizeFeedbackQuanta(feedbackQuanta);
        int normalizedMemory = clampMemoryLimit(maxMemory);
        updating = true;
        planificadorComboBox.setSelectedIndex(policyToIndex(resolvedPolicy));
        velocidadSlider.setValue((int) clampedDuration);
        velocidadValorLabel.setText(formatDuration(clampedDuration));
        quantumSpinner.setValue(normalizedQuantum);
        applyFeedbackQuanta(normalizedFeedbackQuanta);
        memoriaSpinner.setValue(normalizedMemory);
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

    /**
     * Atiende el clic sobre el botón que genera procesos aleatorios.
     */
    private void handleRandomProcessesRequest() {
        if (randomProcessesListener != null) {
            randomProcessesListener.run();
        }
    }

    /**
     * Atiende el clic sobre el botón Iniciar y delega la acción registrada.
     */
    private void handleStartClick() {
        if (startListener != null) {
            startListener.run();
        }
    }

    /**
     * Atiende el clic sobre el botón Pausar y delega la acción registrada.
     */
    private void handlePauseClick() {
        if (pauseListener != null) {
            pauseListener.run();
        }
    }

    /**
     * Atiende el clic sobre el botón Reiniciar y delega la acción registrada.
     */
    private void handleResetClick() {
        if (resetListener != null) {
            resetListener.run();
        }
    }

    private int policyToIndex(PolicyType policy) {
        if (policy == null) {
            return 0;
        }
        switch (policy) {
            case FCFS:
                return 0;
            case SRT:
                return 1;
            case SPN:
                return 2;
            case HRRN:
                return 3;
            case ROUND_ROBIN:
                return 4;
            case FEEDBACK:
                return 5;
            default:
                return 0;
        }
    }

    private PolicyType selectionToPolicy(String value) {
        if (value == null) {
            return PolicyType.FCFS;
        }
        switch (value) {
            case "SRTF":
                return PolicyType.SRT;
            case "SPN":
                return PolicyType.SPN;
            case "HRRN":
                return PolicyType.HRRN;
            case "Round Robin":
                return PolicyType.ROUND_ROBIN;
            case "Feedback":
                return PolicyType.FEEDBACK;
            default:
                return PolicyType.FCFS;
        }
    }

    private long sliderToDuration(int sliderValue) {
        return Math.max(MIN_SPEED, Math.min(MAX_SPEED, sliderValue));
    }

    private String formatDuration(long duration) {
        return duration + " ms";
    }

    /**
     * Ajusta el valor recibido de memoria para respetar los límites configurados.
     * @param candidate valor original ingresado
     * @return valor acotado al rango permitido
     */
    private int clampMemoryLimit(int candidate) {
        if (candidate < MIN_MEMORY_LIMIT) {
            return MIN_MEMORY_LIMIT;
        }
        if (candidate > MAX_MEMORY_LIMIT) {
            return MAX_MEMORY_LIMIT;
        }
        return candidate;
    }

    private int[] normalizeFeedbackQuanta(int[] candidate) {
        int[] values = Arrays.copyOf(DEFAULT_FEEDBACK_QUANTA, DEFAULT_FEEDBACK_QUANTA.length);
        if (candidate == null) {
            return values;
        }
        for (int i = 0; i < values.length && i < candidate.length; i++) {
            int value = candidate[i];
            if (value < 1) {
                value = 1;
            } else if (value > 50) {
                value = 50;
            }
            values[i] = value;
        }
        return values;
    }

    private void applyFeedbackQuanta(int[] values) {
        if (values.length > 0) {
            feedbackQuantumSpinner0.setValue(values[0]);
        }
        if (values.length > 1) {
            feedbackQuantumSpinner1.setValue(values[1]);
        }
        if (values.length > 2) {
            feedbackQuantumSpinner2.setValue(values[2]);
        }
        if (values.length > 3) {
            feedbackQuantumSpinner3.setValue(values[3]);
        }
    }

    private int[] readFeedbackQuanta() {
        return new int[]{
            ((Number) feedbackQuantumSpinner0.getValue()).intValue(),
            ((Number) feedbackQuantumSpinner1.getValue()).intValue(),
            ((Number) feedbackQuantumSpinner2.getValue()).intValue(),
            ((Number) feedbackQuantumSpinner3.getValue()).intValue()
        };
    }

    private void updateQuantumControls(PolicyType policy) {
        boolean rrEnabled = policy == PolicyType.ROUND_ROBIN;
        quantumLabel.setEnabled(rrEnabled);
        quantumSpinner.setEnabled(rrEnabled);
        boolean feedbackEnabled = policy == PolicyType.FEEDBACK;
        setFeedbackControlsEnabled(feedbackEnabled);
    }

    private void setFeedbackControlsEnabled(boolean enabled) {
        feedbackLabel.setEnabled(enabled);
        feedbackNivel0Label.setEnabled(enabled);
        feedbackNivel1Label.setEnabled(enabled);
        feedbackNivel2Label.setEnabled(enabled);
        feedbackNivel3Label.setEnabled(enabled);
        feedbackQuantumSpinner0.setEnabled(enabled);
        feedbackQuantumSpinner1.setEnabled(enabled);
        feedbackQuantumSpinner2.setEnabled(enabled);
        feedbackQuantumSpinner3.setEnabled(enabled);
        feedbackPanel.setEnabled(enabled);
    }

    private void handleFeedbackQuantumChange() {
        if (updating || feedbackQuantumListener == null || !feedbackQuantumSpinner0.isEnabled()) {
            return;
        }
        feedbackQuantumListener.accept(readFeedbackQuanta());
    }

    /**
     * Atiende los cambios en el límite máximo de procesos almacenados en memoria.
     */
    private void handleMaxMemoryChange() {
        if (updating || maxMemoryListener == null) {
            return;
        }
        int limit = ((Number) memoriaSpinner.getValue()).intValue();
        maxMemoryListener.accept(limit);
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

    /**
     * Ajusta la habilitación de los botones según el estado de la simulación.
     */
    private void updateSimulationButtons() {
        iniciarButton.setEnabled(!simulationRunning);
        pausarButton.setEnabled(simulationRunning);
        reiniciarButton.setEnabled(simulationStarted && !simulationRunning);
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregarAleatoriosButton;
    private javax.swing.JLabel arriboLabel;
    private javax.swing.JSpinner arriboSpinner;
    private javax.swing.JButton crearProcesoButton;
    private javax.swing.JLabel feedbackLabel;
    private javax.swing.JLabel feedbackNivel0Label;
    private javax.swing.JLabel feedbackNivel1Label;
    private javax.swing.JLabel feedbackNivel2Label;
    private javax.swing.JLabel feedbackNivel3Label;
    private javax.swing.JPanel feedbackPanel;
    private javax.swing.JSpinner feedbackQuantumSpinner0;
    private javax.swing.JSpinner feedbackQuantumSpinner1;
    private javax.swing.JSpinner feedbackQuantumSpinner2;
    private javax.swing.JSpinner feedbackQuantumSpinner3;
    private javax.swing.JButton iniciarButton;
    private javax.swing.JLabel instruccionesLabel;
    private javax.swing.JSpinner instruccionesSpinner;
    private javax.swing.JLabel ioCycleLabel;
    private javax.swing.JSpinner ioCycleSpinner;
    private javax.swing.JLabel ioDurationLabel;
    private javax.swing.JSpinner ioDurationSpinner;
    private javax.swing.JLabel nombreLabel;
    private javax.swing.JTextField nombreTextField;
    private javax.swing.JButton pausarButton;
    private javax.swing.JComboBox<String> planificadorComboBox;
    private javax.swing.JLabel planificadorLabel;
    private javax.swing.JLabel procesosLabel;
    private javax.swing.JLabel quantumLabel;
    private javax.swing.JSpinner quantumSpinner;
    private javax.swing.JButton reiniciarButton;
    private javax.swing.JComboBox<String> tipoComboBox;
    private javax.swing.JLabel tipoLabel;
    private javax.swing.JLabel tituloLabel;
    private javax.swing.JLabel memoriaLabel;
    private javax.swing.JSpinner memoriaSpinner;
    private javax.swing.JLabel velocidadLabel;
    private javax.swing.JSlider velocidadSlider;
    private javax.swing.JLabel velocidadValorLabel;
    // End of variables declaration//GEN-END:variables
}
