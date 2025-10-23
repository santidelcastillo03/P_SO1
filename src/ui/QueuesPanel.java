/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui;

/**
 * Panel encargado de mostrar las colas de planificación del simulador.
 * Presenta un resumen básico mientras se integra la lógica en tiempo real.
 *
 * @author santiagodelcastillo
 */
public class QueuesPanel extends javax.swing.JPanel {

    private final javax.swing.DefaultListModel<String> readyListModel;
    private final javax.swing.DefaultListModel<String> blockedListModel;
    private final javax.swing.DefaultListModel<String> finishedListModel;
    private final javax.swing.DefaultListModel<String> suspendedListModel;

    /**
     * Construye el panel que mostrará las colas de planificación.
     */
    public QueuesPanel() {
        readyListModel = new javax.swing.DefaultListModel<>();
        blockedListModel = new javax.swing.DefaultListModel<>();
        finishedListModel = new javax.swing.DefaultListModel<>();
        suspendedListModel = new javax.swing.DefaultListModel<>();
        initComponents();
        readyListModel.addElement("Sin procesos");
        blockedListModel.addElement("Sin procesos");
        finishedListModel.addElement("Sin procesos");
        suspendedListModel.addElement("Sin procesos");
    }

    /**
     * Este método es invocado por el constructor para inicializar los componentes.
     * ADVERTENCIA: no modificar manualmente; el editor de formularios lo regenera.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contenedorPrincipal = new javax.swing.JPanel();
        readyPanel = new javax.swing.JPanel();
        readyLabel = new javax.swing.JLabel();
        readyScroll = new javax.swing.JScrollPane();
        readyList = new javax.swing.JList<>();
        blockedPanel = new javax.swing.JPanel();
        blockedLabel = new javax.swing.JLabel();
        blockedScroll = new javax.swing.JScrollPane();
        blockedList = new javax.swing.JList<>();
        finishedPanel = new javax.swing.JPanel();
        finishedLabel = new javax.swing.JLabel();
        finishedScroll = new javax.swing.JScrollPane();
        finishedList = new javax.swing.JList<>();
        suspendedPanel = new javax.swing.JPanel();
        suspendedLabel = new javax.swing.JLabel();
        suspendedScroll = new javax.swing.JScrollPane();
        suspendedList = new javax.swing.JList<>();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Colas de Procesos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, getFont().deriveFont(java.awt.Font.BOLD)));
        setLayout(new java.awt.BorderLayout());
        setPreferredSize(new java.awt.Dimension(400, 200));

        contenedorPrincipal.setOpaque(false);
        contenedorPrincipal.setLayout(new java.awt.GridLayout(1, 4, 12, 0));

        readyPanel.setOpaque(false);
        readyPanel.setLayout(new java.awt.BorderLayout());

        readyLabel.setFont(getFont().deriveFont(java.awt.Font.BOLD, 13f));
        readyLabel.setText("Listos");
        readyLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        readyPanel.add(readyLabel, java.awt.BorderLayout.NORTH);

        readyList.setModel(readyListModel);
        readyList.setFocusable(false);
        readyList.setFont(readyList.getFont().deriveFont(java.awt.Font.PLAIN, 12f));
        readyList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        readyScroll.setViewportView(readyList);

        readyPanel.add(readyScroll, java.awt.BorderLayout.CENTER);

        contenedorPrincipal.add(readyPanel);

        blockedPanel.setOpaque(false);
        blockedPanel.setLayout(new java.awt.BorderLayout());

        blockedLabel.setFont(getFont().deriveFont(java.awt.Font.BOLD, 13f));
        blockedLabel.setText("Bloqueados");
        blockedLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        blockedPanel.add(blockedLabel, java.awt.BorderLayout.NORTH);

        blockedList.setModel(blockedListModel);
        blockedList.setFocusable(false);
        blockedList.setFont(blockedList.getFont().deriveFont(java.awt.Font.PLAIN, 12f));
        blockedList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        blockedScroll.setViewportView(blockedList);

        blockedPanel.add(blockedScroll, java.awt.BorderLayout.CENTER);

        contenedorPrincipal.add(blockedPanel);

        finishedPanel.setOpaque(false);
        finishedPanel.setLayout(new java.awt.BorderLayout());

        finishedLabel.setFont(getFont().deriveFont(java.awt.Font.BOLD, 13f));
        finishedLabel.setText("Terminados");
        finishedLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        finishedPanel.add(finishedLabel, java.awt.BorderLayout.NORTH);

        finishedList.setModel(finishedListModel);
        finishedList.setFocusable(false);
        finishedList.setFont(finishedList.getFont().deriveFont(java.awt.Font.PLAIN, 12f));
        finishedList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        finishedScroll.setViewportView(finishedList);

        finishedPanel.add(finishedScroll, java.awt.BorderLayout.CENTER);

        contenedorPrincipal.add(finishedPanel);

        suspendedPanel.setOpaque(false);
        suspendedPanel.setLayout(new java.awt.BorderLayout());

        suspendedLabel.setFont(getFont().deriveFont(java.awt.Font.BOLD, 13f));
        suspendedLabel.setText("Suspendidos");
        suspendedLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        suspendedPanel.add(suspendedLabel, java.awt.BorderLayout.NORTH);

        suspendedList.setModel(suspendedListModel);
        suspendedList.setFocusable(false);
        suspendedList.setFont(suspendedList.getFont().deriveFont(java.awt.Font.PLAIN, 12f));
        suspendedList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        suspendedScroll.setViewportView(suspendedList);

        suspendedPanel.add(suspendedScroll, java.awt.BorderLayout.CENTER);

        contenedorPrincipal.add(suspendedPanel);

        add(contenedorPrincipal, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    public void updateQueueViews(java.util.List<core.ProcessControlBlock> ready,
                                 java.util.List<core.ProcessControlBlock> blocked,
                                 java.util.List<core.ProcessControlBlock> finished,
                                 java.util.List<core.ProcessControlBlock> readySuspended,
                                 java.util.List<core.ProcessControlBlock> blockedSuspended) {
        java.lang.Runnable task = () -> {
            fillModel(readyListModel, ready);
            fillModel(blockedListModel, blocked);
            fillModel(finishedListModel, finished);
            fillSuspendedModel(suspendedListModel, readySuspended, blockedSuspended);
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(task);
        }
    }

    private void fillModel(javax.swing.DefaultListModel<String> model,
                           java.util.List<core.ProcessControlBlock> processes) {
        model.clear();
        if (processes == null || processes.isEmpty()) {
            model.addElement("Sin procesos");
            return;
        }
        for (core.ProcessControlBlock pcb : processes) {
            if (pcb == null) {
                continue;
            }
            model.addElement(formatProcess(pcb));
        }
    }

    private void fillSuspendedModel(javax.swing.DefaultListModel<String> model,
                                    java.util.List<core.ProcessControlBlock> readySuspended,
                                    java.util.List<core.ProcessControlBlock> blockedSuspended) {
        model.clear();
        boolean hasData = false;
        if (readySuspended != null && !readySuspended.isEmpty()) {
            hasData = true;
            for (core.ProcessControlBlock pcb : readySuspended) {
                if (pcb != null) {
                    model.addElement("Listo-Susp: " + formatProcess(pcb));
                }
            }
        }
        if (blockedSuspended != null && !blockedSuspended.isEmpty()) {
            hasData = true;
            for (core.ProcessControlBlock pcb : blockedSuspended) {
                if (pcb != null) {
                    model.addElement("Bloq-Susp: " + formatProcess(pcb));
                }
            }
        }
        if (!hasData) {
            model.addElement("Sin procesos");
        }
    }

    private String formatProcess(core.ProcessControlBlock pcb) {
        String nombre = pcb.getProcessName();
        int pid = pcb.getProcessId();
        int restante = Math.max(0, pcb.getTotalInstructions() - pcb.getProgramCounter());
        String formatted = String.format("%s (PID=%d | Restante=%d)", nombre, pid, restante);
        return truncateText(formatted, 35);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel blockedLabel;
    private javax.swing.JList<String> blockedList;
    private javax.swing.JPanel blockedPanel;
    private javax.swing.JScrollPane blockedScroll;
    private javax.swing.JPanel contenedorPrincipal;
    private javax.swing.JLabel finishedLabel;
    private javax.swing.JList<String> finishedList;
    private javax.swing.JPanel finishedPanel;
    private javax.swing.JScrollPane finishedScroll;
    private javax.swing.JLabel readyLabel;
    private javax.swing.JList<String> readyList;
    private javax.swing.JPanel readyPanel;
    private javax.swing.JScrollPane readyScroll;
    private javax.swing.JLabel suspendedLabel;
    private javax.swing.JList<String> suspendedList;
    private javax.swing.JPanel suspendedPanel;
    private javax.swing.JScrollPane suspendedScroll;
    // End of variables declaration//GEN-END:variables

    /**
     * Trunca texto largo para evitar que expanda el panel.
     * @param text texto original
     * @param maxLength longitud máxima permitida
     * @return texto truncado con elipsis si es necesario
     */
    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
