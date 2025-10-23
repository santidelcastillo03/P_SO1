/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Jfreechart encapsula una version ligera de las colecciones XYSeries utilizadas
 * para gestionar los puntos que se grafican en el panel de metricas sin depender
 * de bibliotecas externas.
 */
public final class Jfreechart {

    /**
     * Constructor privado para evitar instanciacion accidental.
     */
    private Jfreechart() {
        // Utilidad estatica.
    }

    /**
     * XYSeries representa una serie de puntos (x,y) con un limite de elementos
     * que mantiene unicamente los datos mas recientes.
     */
    public static final class XYSeries {

        private final String key;
        private final int maxItemCount;
        private final Deque<Point2D.Double> points;

        /**
         * Construye una serie con un identificador logico y un tamano maximo.
         * @param key nombre de la serie para identificacion y leyenda
         * @param maxItemCount cantidad maxima de puntos retenidos
         */
        public XYSeries(String key, int maxItemCount) {
            this.key = Objects.requireNonNull(key, "La clave de la serie es obligatoria");
            if (maxItemCount <= 0) {
                throw new IllegalArgumentException("La serie debe retener al menos un punto");
            }
            this.maxItemCount = maxItemCount;
            this.points = new ArrayDeque<>(maxItemCount);
        }

        /**
         * Agrega un punto ordenado por ciclo y descarta los mas antiguos si se supera el limite.
         * @param x valor horizontal (normalmente ciclo de reloj)
         * @param y valor vertical asociado a la metrica
         */
        public synchronized void add(double x, double y) {
            if (!Double.isFinite(x) || !Double.isFinite(y)) {
                return;
            }
            if (points.size() == maxItemCount) {
                points.removeFirst();
            }
            points.addLast(new Point2D.Double(x, y));
        }

        /**
         * Elimina todos los puntos almacenados en la serie.
         */
        public synchronized void clear() {
            points.clear();
        }

        /**
         * Construye una copia inmutable de los puntos actuales para consumo de UI.
         * @return instantanea con los valores de la serie
         */
        public synchronized XYSeriesSnapshot snapshot() {
            int size = points.size();
            double[] xs = new double[size];
            double[] ys = new double[size];
            int index = 0;
            for (Point2D.Double point : points) {
                xs[index] = point.getX();
                ys[index] = point.getY();
                index++;
            }
            return new XYSeriesSnapshot(key, xs, ys);
        }

        /**
         * Recupera la clave asociada a la serie.
         * @return identificador textual de la serie
         */
        public String getKey() {
            return key;
        }
    }

    /**
     * XYSeriesSnapshot almacena una copia desacoplada de los puntos para evitar
     * condiciones de carrera durante la renderizacion.
     */
    public static final class XYSeriesSnapshot {

        private final String key;
        private final double[] xValues;
        private final double[] yValues;

        /**
         * Construye la instantanea con los arreglos de datos suministrados.
         * @param key identificador de la serie
         * @param xValues valores horizontales (ya copiados)
         * @param yValues valores verticales (ya copiados)
         */
        public XYSeriesSnapshot(String key, double[] xValues, double[] yValues) {
            this.key = Objects.requireNonNull(key, "La clave de la serie es obligatoria");
            this.xValues = Objects.requireNonNull(xValues, "Los valores de x son obligatorios");
            this.yValues = Objects.requireNonNull(yValues, "Los valores de y son obligatorios");
        }

        /**
         * Devuelve la clave de identificacion de la serie.
         * @return clave textual asociada
         */
        public String getKey() {
            return key;
        }

        /**
         * Obtiene los valores horizontales capturados.
         * @return arreglo con los valores de x
         */
        public double[] getXValues() {
            return xValues;
        }

        /**
         * Obtiene los valores verticales capturados.
         * @return arreglo con los valores de y
         */
        public double[] getYValues() {
            return yValues;
        }

        /**
         * Indica si la serie contenia puntos al momento de la copia.
         * @return true cuando no se registraron datos
         */
        public boolean isEmpty() {
            return xValues.length == 0;
        }
    }

    /**
     * XYSeriesCollection agrupa varias series y permite obtener instantaneas
     * consistentes de todas ellas sin exponer la estructura interna.
     */
    public static final class XYSeriesCollection {

        private final List<XYSeries> seriesList;

        /**
         * Inicializa la coleccion vacia.
         */
        public XYSeriesCollection() {
            this.seriesList = new ArrayList<>();
        }

        /**
         * Anade una serie a la coleccion para ser gestionada.
         * @param series serie que se desea almacenar
         */
        public synchronized void addSeries(XYSeries series) {
            seriesList.add(Objects.requireNonNull(series, "La serie no puede ser nula"));
        }

        /**
         * Busca una serie existente por su clave.
         * @param key identificador textual de la serie
         * @return serie encontrada o null si no existe
         */
        public synchronized XYSeries findSeries(String key) {
            for (XYSeries series : seriesList) {
                if (series.getKey().equals(key)) {
                    return series;
                }
            }
            return null;
        }

        /**
         * Obtiene una copia independiente de todas las series para su renderizado.
         * @return lista de instantaneas de las series
         */
        public synchronized List<XYSeriesSnapshot> snapshot() {
            List<XYSeriesSnapshot> copy = new ArrayList<>(seriesList.size());
            for (XYSeries series : seriesList) {
                copy.add(series.snapshot());
            }
            return copy;
        }

        /**
         * Limpia todas las series administradas sin eliminarlas de la coleccion.
         */
        public synchronized void clearAll() {
            for (XYSeries series : seriesList) {
                series.clear();
            }
        }
    }
}
