/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package p_so1;

import ui.NewMainFrame;

/**
 * Simulador de Planificación de Procesos con interfaz gráfica.
 * 
 *
 * @author angel
 */
public class P_so1 {

    
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewMainFrame().setVisible(true);
            }
        });
    }
}
