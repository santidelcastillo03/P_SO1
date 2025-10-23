/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui;

import core.OperatingSystem;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import util.Jfreechart;
import util.Jfreechart.XYSeries;
import util.Jfreechart.XYSeriesCollection;
import util.Jfreechart.XYSeriesSnapshot;
import util.MetricsCalculator;

/**
 * ChartPanel renderiza en tiempo real la evolucion de las metricas principales
 * del simulador utilizando una implementacion ligera tipo XYSeries.
 */
public class ChartPanel extends javax.swing.JPanel {

    private static final int MAX_SAMPLE_POINTS = 240;
    private static final long SAMPLE_STEP_CYCLES = 5L;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.###");
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color GRID_COLOR = new Color(0, 0, 0, 35);
    private static final Color AXIS_COLOR = new Color(40, 40, 40, 200);
    private static final Color TEXT_COLOR = new Color(38, 38, 38);
    private static final Color LEGEND_BACKGROUND_COLOR = new Color(255, 255, 255, 230);
    private static final Color LEGEND_BORDER_COLOR = new Color(0, 0, 0, 60);

    private final XYSeriesCollection dataset;
    private final Map<String, XYSeries> seriesIndex;
    private final List<SeriesDescriptor> seriesDescriptors;
    private final Map<String, Double> latestValues;
    private final MetricsChartCanvas chartCanvas;
    private volatile MetricsCalculator metricsCalculator;
    private volatile long lastSampledCycle;

    /**
     * Construye el panel destinado a la visualizacion de tiempos y metricas.
     */
    public ChartPanel() {
        initComponents();
        setLayout(new BorderLayout());
        this.dataset = new XYSeriesCollection();
        this.seriesIndex = new LinkedHashMap<>();
        this.seriesDescriptors = new ArrayList<>();
        this.latestValues = new LinkedHashMap<>();
        this.chartCanvas = new MetricsChartCanvas();
        this.lastSampledCycle = -1L;
        initializeSeries();
        add(chartCanvas, BorderLayout.CENTER);
    }

    /**
     * Vincula el panel al sistema operativo activo para acceder al calculador de metricas.
     * @param operatingSystem instancia del simulador o null para desasociar
     */
    public void bindOperatingSystem(OperatingSystem operatingSystem) {
        MetricsCalculator calculator = operatingSystem != null ? operatingSystem.getMetricsCalculator() : null;
        Runnable task = () -> {
            metricsCalculator = calculator;
            lastSampledCycle = -1L;
            dataset.clearAll();
            for (String key : latestValues.keySet()) {
                latestValues.put(key, Double.NaN);
            }
            chartCanvas.repaint();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    /**
     * Registra una muestra de metricas cuando se alcanza el intervalo configurado.
     * @param cycle ciclo global vigente al momento de invocar
     */
    public void registerMetricsSample(long cycle) {
        MetricsCalculator calculator = metricsCalculator;
        if (calculator == null || cycle < 0L) {
            return;
        }
        long previous = lastSampledCycle;
        if (previous >= 0L && (cycle - previous) < SAMPLE_STEP_CYCLES) {
            return;
        }
        lastSampledCycle = cycle;
        final double throughput = calculator.getThroughput();
        final double avgResponse = calculator.getAvgResponseTime();

        Runnable task = () -> {
            addSample("throughput", cycle, throughput);
            addSample("avgResponse", cycle, avgResponse);
            chartCanvas.repaint();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    /**
     * Inicializa todas las series a seguir con su color y etiqueta.
     */
    private void initializeSeries() {
        registerSeries("throughput", "Rendimiento", new Color(80, 160, 255), 100d, 1d, " proc/ciclo");
        registerSeries("avgResponse", "Tiempo resp.", new Color(231, 76, 60), 1d, 1d, " ciclos");
    }

    /**
     * Registra una serie dentro de la coleccion y establece su configuracion visual.
     * @param key clave interna de la serie
     * @param label etiqueta mostrada en la leyenda
     * @param color color utilizado para la linea y marcadores
     */
    private void registerSeries(String key,
                                String label,
                                Color color,
                                double chartScale,
                                double legendMultiplier,
                                String legendSuffix) {
        XYSeries series = new Jfreechart.XYSeries(key, MAX_SAMPLE_POINTS);
        dataset.addSeries(series);
        seriesIndex.put(key, series);
        seriesDescriptors.add(new SeriesDescriptor(key, label, color, chartScale, legendMultiplier, legendSuffix));
        latestValues.put(key, Double.NaN);
    }

    /**
     * Agrega un punto a la serie indicada y actualiza el valor mas reciente.
     * @param key identificador de la serie
     * @param cycle ciclo asociado al punto
     * @param value valor de la metrica en ese ciclo
     */
    private void addSample(String key, double cycle, double value) {
        SeriesDescriptor descriptor = findDescriptor(key);
        if (descriptor == null) {
            return;
        }
        XYSeries series = seriesIndex.get(key);
        if (series == null) {
            return;
        }
        series.add(cycle, descriptor.scaleForChart(value));
        latestValues.put(key, value);
    }

    /**
     * Formatea un numero en texto amigable para la leyenda y los ejes.
     * @param value valor numerico a formatear
     * @return cadena corta con el valor representado
     */
    private String formatValue(double value) {
        if (!Double.isFinite(value)) {
            return "--";
        }
        if (Math.abs(value) >= 1000d) {
            return String.format("%.0f", value);
        }
        return DECIMAL_FORMAT.format(value);
    }

    /**
     * Busca la configuracion visual asociada a una serie.
     * @param key clave de la serie
     * @return descriptor registrado o null si no existe
     */
    private SeriesDescriptor findDescriptor(String key) {
        for (SeriesDescriptor descriptor : seriesDescriptors) {
            if (descriptor.key.equals(key)) {
                return descriptor;
            }
        }
        return null;
    }

    /**
     * Este metodo es invocado por el constructor para inicializar los componentes.
     * ADVERTENCIA: no modificar manualmente; el editor de formularios lo regenera.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 559, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * SeriesDescriptor guarda metadatos visuales para cada linea del grafico.
     */
    private static final class SeriesDescriptor {
        private final String key;
        private final String label;
        private final Color color;
        private final double chartScale;
        private final double legendMultiplier;
        private final String legendSuffix;

        /**
         * Crea el descriptor con clave, etiqueta y color.
         * @param key identificador interno
         * @param label etiqueta mostrada al usuario
         * @param color color asociado a la serie
         */
        private SeriesDescriptor(String key,
                                 String label,
                                 Color color,
                                 double chartScale,
                                 double legendMultiplier,
                                 String legendSuffix) {
            this.key = key;
            this.label = label;
            this.color = color;
            this.chartScale = chartScale <= 0d ? 1d : chartScale;
            this.legendMultiplier = legendMultiplier;
            this.legendSuffix = legendSuffix != null ? legendSuffix : "";
        }

        /**
         * Escala el valor para su representacion en el grafico.
         * @param raw valor original medido
         * @return valor escalado
         */
        private double scaleForChart(double raw) {
            if (!Double.isFinite(raw)) {
                return Double.NaN;
            }
            return raw * chartScale;
        }

        /**
         * Formatea el valor para mostrarse en la leyenda.
         * @param raw valor original medido
         * @param formatter funcion que aplica el formato numerico
         * @return texto formateado
         */
        private String legendText(double raw, java.util.function.DoubleFunction<String> formatter) {
            if (!Double.isFinite(raw)) {
                return "--";
            }
            double adjusted = raw * legendMultiplier;
            return formatter.apply(adjusted) + legendSuffix;
        }
    }

    /**
     * MetricsChartCanvas es el componente responsable de pintar las series y ejes.
     */
    private final class MetricsChartCanvas extends JPanel {

        private static final int HORIZONTAL_GRID_STEPS = 5;
        private static final int VERTICAL_GRID_STEPS = 4;

        /**
         * Construye el canvas con las propiedades de renderizado adecuadas.
         */
        private MetricsChartCanvas() {
            setOpaque(true);
            setDoubleBuffered(true);
            setBackground(BACKGROUND_COLOR);
        }

        /**
         * Renderiza el grafico completo respetando las metricas actuales.
         * @param g contexto grafico provisto por Swing
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRect(0, 0, width, height);

                List<XYSeriesSnapshot> snapshots = dataset.snapshot();
                boolean hasData = false;
                double minX = Double.POSITIVE_INFINITY;
                double maxX = Double.NEGATIVE_INFINITY;
                double minY = Double.POSITIVE_INFINITY;
                double maxY = Double.NEGATIVE_INFINITY;

                for (XYSeriesSnapshot snapshot : snapshots) {
                    if (snapshot.isEmpty()) {
                        continue;
                    }
                    hasData = true;
                    for (double xValue : snapshot.getXValues()) {
                        if (!Double.isFinite(xValue)) {
                            continue;
                        }
                        minX = Math.min(minX, xValue);
                        maxX = Math.max(maxX, xValue);
                    }
                    for (double yValue : snapshot.getYValues()) {
                        if (!Double.isFinite(yValue)) {
                            continue;
                        }
                        minY = Math.min(minY, yValue);
                        maxY = Math.max(maxY, yValue);
                    }
                }

                if (!hasData) {
                    drawEmptyState(g2, width, height);
                    return;
                }

                if (Double.compare(minX, maxX) == 0) {
                    minX -= 1d;
                    maxX += 1d;
                }
                if (Double.compare(minY, maxY) == 0) {
                    double delta = Math.max(1d, Math.abs(minY) * 0.1d);
                    minY -= delta;
                    maxY += delta;
                }

                int leftMargin = 68;
                int rightMargin = 32;
                int topMargin = 32;
                int bottomMargin = 52;

                int plotWidth = Math.max(1, width - leftMargin - rightMargin);
                int plotHeight = Math.max(1, height - topMargin - bottomMargin);
                int plotX = leftMargin;
                int plotY = topMargin;

                drawGrid(g2, plotX, plotY, plotWidth, plotHeight);
                for (XYSeriesSnapshot snapshot : snapshots) {
                    if (snapshot.isEmpty()) {
                        continue;
                    }
                    SeriesDescriptor descriptor = findDescriptor(snapshot.getKey());
                    if (descriptor == null) {
                        continue;
                    }
                    drawSeries(g2, descriptor, snapshot, plotX, plotY, plotWidth, plotHeight, minX, maxX, minY, maxY);
                }
                drawAxes(g2, plotX, plotY, plotWidth, plotHeight);
                drawAxisLabels(g2, plotX, plotY, plotWidth, plotHeight, minX, maxX, minY, maxY);
                drawLegend(g2, plotX + 8, plotY + 8);
            } finally {
                g2.dispose();
            }
        }

        /**
         * Dibuja el estado vacio cuando no existen datos para mostrar.
         * @param g2 contexto grafico
         * @param width ancho disponible
         * @param height alto disponible
         */
        private void drawEmptyState(Graphics2D g2, int width, int height) {
            g2.setColor(TEXT_COLOR);
            g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
            String text = "Esperando datos de la simulacion";
            FontMetrics metrics = g2.getFontMetrics();
            int textWidth = metrics.stringWidth(text);
            int x = (width - textWidth) / 2;
            int y = height / 2;
            g2.drawString(text, x, y);
        }

        /**
         * Traza la cuadricula de referencia del grafico.
         * @param g2 contexto grafico
         * @param plotX inicio horizontal del area de dibujo
         * @param plotY inicio vertical del area de dibujo
         * @param plotWidth ancho util del grafico
         * @param plotHeight alto util del grafico
         */
        private void drawGrid(Graphics2D g2, int plotX, int plotY, int plotWidth, int plotHeight) {
            g2.setColor(GRID_COLOR);
            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= HORIZONTAL_GRID_STEPS; i++) {
                int y = plotY + (int) Math.round((plotHeight * i) / (double) HORIZONTAL_GRID_STEPS);
                g2.drawLine(plotX, y, plotX + plotWidth, y);
            }
            for (int i = 0; i <= VERTICAL_GRID_STEPS; i++) {
                int x = plotX + (int) Math.round((plotWidth * i) / (double) VERTICAL_GRID_STEPS);
                g2.drawLine(x, plotY, x, plotY + plotHeight);
            }
        }

        /**
         * Pinta una serie conectando sus puntos con lineas suavizadas.
         * @param g2 contexto grafico
         * @param descriptor configuracion visual para la serie
         * @param snapshot puntos a dibujar
         * @param plotX inicio horizontal del area de grafico
         * @param plotY inicio vertical del area de grafico
         * @param plotWidth ancho util
         * @param plotHeight alto util
         * @param minX minimo global en x
         * @param maxX maximo global en x
         * @param minY minimo global en y
         * @param maxY maximo global en y
         */
        private void drawSeries(Graphics2D g2,
                                SeriesDescriptor descriptor,
                                XYSeriesSnapshot snapshot,
                                int plotX,
                                int plotY,
                                int plotWidth,
                                int plotHeight,
                                double minX,
                                double maxX,
                                double minY,
                                double maxY) {
            double[] xs = snapshot.getXValues();
            double[] ys = snapshot.getYValues();
            if (xs.length == 0) {
                return;
            }
            Path2D.Double path = new Path2D.Double();
            int firstX = translateX(xs[0], minX, maxX, plotX, plotWidth);
            int firstY = translateY(ys[0], minY, maxY, plotY, plotHeight);
            path.moveTo(firstX, firstY);
            for (int i = 1; i < xs.length; i++) {
                int x = translateX(xs[i], minX, maxX, plotX, plotWidth);
                int y = translateY(ys[i], minY, maxY, plotY, plotHeight);
                path.lineTo(x, y);
            }
            g2.setColor(descriptor.color);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(path);
        }

        /**
         * Dibuja los ejes principales del grafico.
         * @param g2 contexto grafico
         * @param plotX inicio horizontal del area de grafico
         * @param plotY inicio vertical del area de grafico
         * @param plotWidth ancho util
         * @param plotHeight alto util
         */
        private void drawAxes(Graphics2D g2, int plotX, int plotY, int plotWidth, int plotHeight) {
            g2.setColor(AXIS_COLOR);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(plotX, plotY, plotX, plotY + plotHeight);
            g2.drawLine(plotX, plotY + plotHeight, plotX + plotWidth, plotY + plotHeight);
        }

        /**
         * Muestra los valores minimo y maximo en los ejes para orientar al usuario.
         * @param g2 contexto grafico
         * @param plotX inicio horizontal del area de grafico
         * @param plotY inicio vertical del area de grafico
         * @param plotWidth ancho util
         * @param plotHeight alto util
         * @param minX minimo global en x
         * @param maxX maximo global en x
         * @param minY minimo global en y
         * @param maxY maximo global en y
         */
        private void drawAxisLabels(Graphics2D g2,
                                    int plotX,
                                    int plotY,
                                    int plotWidth,
                                    int plotHeight,
                                    double minX,
                                    double maxX,
                                    double minY,
                                    double maxY) {
            g2.setColor(TEXT_COLOR);
            Font axisFont = getFont().deriveFont(Font.PLAIN, 11f);
            g2.setFont(axisFont);
            FontMetrics metrics = g2.getFontMetrics();
            String minXLabel = "Ciclo " + formatValue(minX);
            String maxXLabel = "Ciclo " + formatValue(maxX);
            int y = plotY + plotHeight + metrics.getAscent() + 6;
            g2.drawString(minXLabel, plotX, y);
            int maxXWidth = metrics.stringWidth(maxXLabel);
            g2.drawString(maxXLabel, plotX + plotWidth - maxXWidth, y);

            String maxYLabel = formatValue(maxY);
            String minYLabel = formatValue(minY);
            int leftX = plotX - metrics.stringWidth(maxYLabel) - 8;
            g2.drawString(maxYLabel, leftX, plotY + metrics.getAscent());
            leftX = plotX - metrics.stringWidth(minYLabel) - 8;
            g2.drawString(minYLabel, leftX, plotY + plotHeight);
        }

        /**
         * Dibuja la leyenda con los valores recientes para cada metrica.
         * @param g2 contexto grafico
         * @param x posicion horizontal inicial
         * @param y posicion vertical inicial
         */
        private void drawLegend(Graphics2D g2, int x, int y) {
            Font legendFont = getFont().deriveFont(Font.PLAIN, 12f);
            g2.setFont(legendFont);
            FontMetrics metrics = g2.getFontMetrics();
            int padding = 8;
            int colorBox = 10;
            int lineHeight = metrics.getHeight();
            int maxWidth = 0;
            List<String> lines = new ArrayList<>(seriesDescriptors.size());
            for (SeriesDescriptor descriptor : seriesDescriptors) {
                Double value = latestValues.get(descriptor.key);
                double rawValue = value != null ? value : Double.NaN;
                String formatted = descriptor.legendText(rawValue, ChartPanel.this::formatValue);
                String text = descriptor.label + ": " + formatted;
                lines.add(text);
                maxWidth = Math.max(maxWidth, metrics.stringWidth(text));
            }
            int boxWidth = padding * 3 + colorBox + maxWidth;
            int boxHeight = padding * 2 + lineHeight * lines.size();
            g2.setColor(LEGEND_BACKGROUND_COLOR);
            g2.fillRoundRect(x, y, boxWidth, boxHeight, 12, 12);
            g2.setColor(LEGEND_BORDER_COLOR);
            g2.drawRoundRect(x, y, boxWidth, boxHeight, 12, 12);

            int textX = x + padding + colorBox + 6;
            int textY = y + padding + metrics.getAscent();
            for (int i = 0; i < seriesDescriptors.size(); i++) {
                SeriesDescriptor descriptor = seriesDescriptors.get(i);
                g2.setColor(descriptor.color);
                int colorX = x + padding;
                int colorY = y + padding + i * lineHeight + (lineHeight / 2) - (colorBox / 2);
                g2.fillRect(colorX, colorY, colorBox, colorBox);

                g2.setColor(TEXT_COLOR);
                g2.drawString(lines.get(i), textX, textY + i * lineHeight);
            }
        }

        /**
         * Convierte un valor horizontal en coordenada de pantalla.
         * @param value valor horizontal de datos
         * @param minX minimo global en x
         * @param maxX maximo global en x
         * @param plotX inicio horizontal del grafico
         * @param plotWidth ancho util del grafico
         * @return coordenada horizontal en pixeles
         */
        private int translateX(double value, double minX, double maxX, int plotX, int plotWidth) {
            double ratio = (value - minX) / (maxX - minX);
            ratio = Math.max(0d, Math.min(1d, ratio));
            return plotX + (int) Math.round(ratio * plotWidth);
        }

        /**
         * Convierte un valor vertical en coordenada de pantalla.
         * @param value valor vertical de datos
         * @param minY minimo global en y
         * @param maxY maximo global en y
         * @param plotY inicio vertical del grafico
         * @param plotHeight alto util del grafico
         * @return coordenada vertical en pixeles
         */
        private int translateY(double value, double minY, double maxY, int plotY, int plotHeight) {
            double ratio = (value - minY) / (maxY - minY);
            ratio = Math.max(0d, Math.min(1d, ratio));
            return plotY + plotHeight - (int) Math.round(ratio * plotHeight);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
