/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui;

import core.OperatingSystem;
import core.ProcessControlBlock;

/**
 * Panel lateral que refleja el estado actual de la CPU simulada.
 * Está listo para mostrar el proceso activo y sus métricas.
 *
 * @author santiagodelcastillo
 */
public class CpuPanel extends javax.swing.JPanel {

    public CpuPanel() {
        initComponents();
        updateCpuView(null, 0L, OperatingSystem.CpuMode.OS);
    }

    /**
     * Este método es invocado por el constructor para inicializar los componentes.
     * ADVERTENCIA: no modificar manualmente; el editor de formularios lo regenera.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        contenedor = new javax.swing.JPanel();
        procesoEtiqueta = new javax.swing.JLabel();
        procesoValor = new javax.swing.JLabel();
        pidEtiqueta = new javax.swing.JLabel();
        pidValor = new javax.swing.JLabel();
        pcEtiqueta = new javax.swing.JLabel();
        pcValor = new javax.swing.JLabel();
        marEtiqueta = new javax.swing.JLabel();
        marValor = new javax.swing.JLabel();
        totalEtiqueta = new javax.swing.JLabel();
        totalValor = new javax.swing.JLabel();
        cicloEtiqueta = new javax.swing.JLabel();
        cicloValor = new javax.swing.JLabel();
        modoEtiqueta = new javax.swing.JLabel();
        modoValor = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CPU", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        setLayout(new java.awt.BorderLayout());

        contenedor.setOpaque(false);
        contenedor.setLayout(new java.awt.GridBagLayout());

        procesoEtiqueta.setText("Proceso:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        contenedor.add(procesoEtiqueta, gridBagConstraints);

        procesoValor.setText("Sin proceso");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 2);
        contenedor.add(procesoValor, gridBagConstraints);

        pidEtiqueta.setText("PID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        contenedor.add(pidEtiqueta, gridBagConstraints);

        pidValor.setText("--");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 2);
        contenedor.add(pidValor, gridBagConstraints);

        pcEtiqueta.setText("PC:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        contenedor.add(pcEtiqueta, gridBagConstraints);

        pcValor.setText("--");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 2);
        contenedor.add(pcValor, gridBagConstraints);

        marEtiqueta.setText("MAR:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        contenedor.add(marEtiqueta, gridBagConstraints);

        marValor.setText("--");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 2);
        contenedor.add(marValor, gridBagConstraints);

        totalEtiqueta.setText("Total:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        contenedor.add(totalEtiqueta, gridBagConstraints);

        totalValor.setText("--");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 2);
        contenedor.add(totalValor, gridBagConstraints);

        cicloEtiqueta.setText("Ciclo actual:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        contenedor.add(cicloEtiqueta, gridBagConstraints);

        cicloValor.setText("--");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 2);
        contenedor.add(cicloValor, gridBagConstraints);

        modoEtiqueta.setText("Modo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        contenedor.add(modoEtiqueta, gridBagConstraints);

        modoValor.setText("OS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 2);
        contenedor.add(modoValor, gridBagConstraints);

        add(contenedor, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    public void updateCpuView(ProcessControlBlock pcb, long clockCycle, OperatingSystem.CpuMode mode) {
        Runnable task = () -> {
            if (pcb == null) {
                procesoValor.setText("Sin proceso");
                pidValor.setText("--");
                pcValor.setText("--");
                marValor.setText("--");
                totalValor.setText("--");
            } else {
                procesoValor.setText(pcb.getProcessName());
                pidValor.setText(String.valueOf(pcb.getProcessId()));
                pcValor.setText(String.valueOf(pcb.getProgramCounter()));
                marValor.setText(String.valueOf(pcb.getMemoryAddressRegister()));
                totalValor.setText(String.valueOf(pcb.getTotalInstructions()));
            }
            cicloValor.setText(String.valueOf(clockCycle));
            OperatingSystem.CpuMode safeMode = mode != null ? mode : OperatingSystem.CpuMode.OS;
            modoValor.setText(safeMode.getDisplayName());
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(task);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cicloEtiqueta;
    private javax.swing.JLabel cicloValor;
    private javax.swing.JPanel contenedor;
    private javax.swing.JLabel marEtiqueta;
    private javax.swing.JLabel marValor;
    private javax.swing.JLabel modoEtiqueta;
    private javax.swing.JLabel modoValor;
    private javax.swing.JLabel pcEtiqueta;
    private javax.swing.JLabel pcValor;
    private javax.swing.JLabel pidEtiqueta;
    private javax.swing.JLabel pidValor;
    private javax.swing.JLabel procesoEtiqueta;
    private javax.swing.JLabel procesoValor;
    private javax.swing.JLabel totalEtiqueta;
    private javax.swing.JLabel totalValor;
    // End of variables declaration//GEN-END:variables
}
