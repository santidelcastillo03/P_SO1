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
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;

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

    /**
     * Obtiene el color asociado a una política específica
     * @param policyName Nombre de la política
     * @return Color asignado a la política
     */
    private static Color getPolicyColor(String policyName) {
        if (policyName == null) {
            return Color.GRAY;
        }

        switch (policyName) {
            case "FCFS":
                return new Color(79, 129, 189);    // Azul
            case "Round Robin":
                return new Color(155, 187, 89);    // Verde
            case "SPN":
                return new Color(192, 80, 77);     // Rojo
            case "SRTF":
                return new Color(128, 100, 162);   // Púrpura
            case "HRRN":
                return new Color(247, 150, 70);    // Naranja
            case "Feedback":
                return new Color(75, 172, 198);    // Cian
            default:
                return Color.GRAY;
        }
    }

    /**
     * Crea un gráfico de barras comparando el throughput de diferentes políticas
     * @param metricsArray Arreglo de PolicyMetrics con los datos de cada política
     * @param arrayLength Longitud del arreglo (cuántas políticas tienen datos)
     * @return ChartPanel con el gráfico de barras
     */
    public static ChartPanel createThroughputBarChart(MetricsCalculator.PolicyMetrics[] metricsArray, int arrayLength) {
        // Crear el dataset - todas las políticas en una sola serie, pero diferentes categorías
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Agregar datos de cada política como categoría separada
        for (int i = 0; i < arrayLength && i < metricsArray.length; i++) {
            MetricsCalculator.PolicyMetrics metrics = metricsArray[i];
            if (metrics != null) {
                double throughput = metrics.getThroughput();
                dataset.addValue(throughput, "Throughput", metrics.getPolicyName());
            }
        }

        // Crear el gráfico de barras
        org.jfree.chart.JFreeChart chart = ChartFactory.createBarChart(
            "Comparación de Throughput por Política de Planificación",  // Título
            "Política",                                                  // Eje X
            "Throughput (procesos/ciclo)",                              // Eje Y
            dataset,                                                     // Datos
            PlotOrientation.VERTICAL,                                   // Orientación
            false,                                                       // Leyenda
            true,                                                        // Tooltips
            false                                                        // URLs
        );

        // Personalizar colores
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Crear un renderer personalizado que asigna colores por categoría
        renderer = new BarRenderer() {
            @Override
            public java.awt.Paint getItemPaint(int row, int column) {
                // row es la serie (siempre 0 en nuestro caso)
                // column es la categoría (la política)
                if (column < arrayLength && column < metricsArray.length) {
                    MetricsCalculator.PolicyMetrics metrics = metricsArray[column];
                    if (metrics != null) {
                        return getPolicyColor(metrics.getPolicyName());
                    }
                }
                return Color.GRAY;
            }
        };

        plot.setRenderer(renderer);

        // Crear el panel del gráfico
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
        chartPanel.setBackground(Color.WHITE);

        return chartPanel;
    }

    /**
     * Crea un gráfico de barras vacío cuando no hay datos de throughput
     * @return ChartPanel con mensaje indicando que no hay datos
     */
    public static ChartPanel createEmptyBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(0, "Throughput", "Sin datos");

        org.jfree.chart.JFreeChart chart = ChartFactory.createBarChart(
            "No hay datos de throughput para mostrar",
            "Política",
            "Throughput",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            false,
            false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.LIGHT_GRAY);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
        chartPanel.setBackground(Color.WHITE);

        return chartPanel;
    }
}
