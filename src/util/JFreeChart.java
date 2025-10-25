/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.Color;
import org.jfree.chart.plot.PiePlot;

/**
 * Utilidad para crear gráficos usando JFreeChart
 * @author santiagodelcastillo
 */
public class JFreeChart {

    /**
     * Crea un gráfico de pastel (pie chart) comparando procesos I/O-bound vs CPU-bound
     * @param ioBoundCount Cantidad de procesos I/O-bound
     * @param cpuBoundCount Cantidad de procesos CPU-bound
     * @return ChartPanel con el gráfico generado
     */
    public static ChartPanel createProcessTypePieChart(int ioBoundCount, int cpuBoundCount) {
        // Crear el dataset
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("I/O-bound (" + ioBoundCount + ")", ioBoundCount);
        dataset.setValue("CPU-bound (" + cpuBoundCount + ")", cpuBoundCount);

        // Crear el gráfico de pastel
        org.jfree.chart.JFreeChart chart = ChartFactory.createPieChart(
            "Comparación de Procesos: I/O-bound vs CPU-bound",  // Título
            dataset,                                             // Datos
            true,                                                // Incluir leyenda
            true,                                                // Incluir tooltips
            false                                                // No incluir URLs
        );

        // Personalizar los colores del gráfico
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("I/O-bound (" + ioBoundCount + ")", new Color(100, 149, 237)); // Azul claro
        plot.setSectionPaint("CPU-bound (" + cpuBoundCount + ")", new Color(255, 127, 80));  // Coral

        // Configurar el fondo del plot
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        // Crear el panel del gráfico
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
        chartPanel.setBackground(Color.WHITE);

        return chartPanel;
    }

    /**
     * Crea un ChartPanel vacío cuando no hay procesos
     * @return ChartPanel con mensaje indicando que no hay datos
     */
    public static ChartPanel createEmptyPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Sin procesos", 1);

        org.jfree.chart.JFreeChart chart = ChartFactory.createPieChart(
            "No hay procesos para mostrar",
            dataset,
            false,
            false,
            false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Sin procesos", Color.LIGHT_GRAY);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
        chartPanel.setBackground(Color.WHITE);

        return chartPanel;
    }
}
