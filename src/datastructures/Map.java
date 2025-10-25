/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datastructures;

/**
 * Implementación de un mapa hash usando encadenamiento separado para resolver colisiones.
 * Proporciona almacenamiento básico de pares clave-valor con redimensionamiento dinámico.
 *
 * @param <K> el tipo de claves mantenidas por este mapa
 * @param <V> el tipo de valores asociados
 * @author santiagodelcastillo
 */
public class Map<K, V> {

    /**
     * Nodo de entrada para almacenar pares clave-valor en la tabla hash.
     */
    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /** Capacidad inicial predeterminada para la tabla hash. */
    private static final int DEFAULT_CAPACITY = 16;

    /** Factor de carga umbral para activar el redimensionamiento. */
    private static final double LOAD_FACTOR = 0.75;

    /** Arreglo de cubetas para almacenar entradas. */
    private Entry<K, V>[] buckets;

    /** Número actual de pares clave-valor en el mapa. */
    private int size;

    /** Umbral para redimensionar basado en el factor de carga. */
    private int threshold;

    /**
     * Construye un mapa vacío con capacidad inicial predeterminada.
     */
    @SuppressWarnings("unchecked")
    public Map() {
        this.buckets = (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
        this.size = 0;
        this.threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
    }

    /**
     * Construye un mapa vacío con la capacidad inicial especificada.
     *
     * @param initialCapacity la capacidad inicial
     * @throws IllegalArgumentException si la capacidad inicial es negativa
     */
    @SuppressWarnings("unchecked")
    public Map(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Capacidad inicial ilegal: " + initialCapacity);
        }
        int capacity = Math.max(1, initialCapacity);
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
        this.size = 0;
        this.threshold = (int) (capacity * LOAD_FACTOR);
    }

    /**
     * Asocia el valor especificado con la clave especificada en este mapa.
     * Si el mapa contenía previamente un mapeo para la clave, el valor anterior es reemplazado.
     *
     * @param key clave con la cual el valor especificado será asociado
     * @param value valor a ser asociado con la clave especificada
     * @return el valor anterior asociado con la clave, o null si no había mapeo
     */
    public V put(K key, V value) {
        if (key == null) {
            return putForNullKey(value);
        }

        int hash = hash(key);
        int index = indexFor(hash, buckets.length);

        // Verificar si la clave ya existe
        for (Entry<K, V> entry = buckets[index]; entry != null; entry = entry.next) {
            if (entry.key.equals(key)) {
                V oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
        }

        // Agregar nueva entrada
        addEntry(key, value, index);
        return null;
    }

    /**
     * Devuelve el valor al cual la clave especificada está mapeada,
     * o null si este mapa no contiene mapeo para la clave.
     *
     * @param key la clave cuyo valor asociado será devuelto
     * @return el valor al cual la clave especificada está mapeada, o null
     */
    public V get(K key) {
        if (key == null) {
            return getForNullKey();
        }

        int hash = hash(key);
        int index = indexFor(hash, buckets.length);

        for (Entry<K, V> entry = buckets[index]; entry != null; entry = entry.next) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    /**
     * Elimina el mapeo para la clave especificada de este mapa si está presente.
     *
     * @param key clave cuyo mapeo será eliminado del mapa
     * @return el valor anterior asociado con la clave, o null si no había mapeo
     */
    public V remove(K key) {
        if (key == null) {
            return removeForNullKey();
        }

        int hash = hash(key);
        int index = indexFor(hash, buckets.length);

        Entry<K, V> prev = null;
        Entry<K, V> entry = buckets[index];

        while (entry != null) {
            if (entry.key.equals(key)) {
                if (prev == null) {
                    buckets[index] = entry.next;
                } else {
                    prev.next = entry.next;
                }
                size--;
                return entry.value;
            }
            prev = entry;
            entry = entry.next;
        }
        return null;
    }

    /**
     * Devuelve true si este mapa contiene un mapeo para la clave especificada.
     *
     * @param key clave cuya presencia en este mapa será probada
     * @return true si este mapa contiene un mapeo para la clave especificada
     */
    public boolean containsKey(K key) {
        return get(key) != null || (key == null && getForNullKey() != null);
    }

    /**
     * Devuelve el número de mapeos clave-valor en este mapa.
     *
     * @return el número de mapeos clave-valor en este mapa
     */
    public int size() {
        return size;
    }

    /**
     * Devuelve true si este mapa no contiene mapeos clave-valor.
     *
     * @return true si este mapa no contiene mapeos clave-valor
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Elimina todos los mapeos de este mapa.
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        this.buckets = (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
        this.size = 0;
        this.threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
    }

    /**
     * Devuelve un arreglo que contiene todas las claves en este mapa.
     *
     * @return un arreglo de todas las claves
     */
    @SuppressWarnings("unchecked")
    public K[] keys() {
        K[] result = (K[]) new Object[size];
        int index = 0;
        for (Entry<K, V> bucket : buckets) {
            for (Entry<K, V> entry = bucket; entry != null; entry = entry.next) {
                result[index++] = entry.key;
            }
        }
        return result;
    }

    /**
     * Devuelve un arreglo que contiene todos los valores en este mapa.
     *
     * @return un arreglo de todos los valores
     */
    @SuppressWarnings("unchecked")
    public V[] values() {
        V[] result = (V[]) new Object[size];
        int index = 0;
        for (Entry<K, V> bucket : buckets) {
            for (Entry<K, V> entry = bucket; entry != null; entry = entry.next) {
                result[index++] = entry.value;
            }
        }
        return result;
    }

    /**
     * Calcula el código hash para la clave dada.
     */
    private int hash(K key) {
        int h = key.hashCode();
        // Dispersar bits para mejor distribución
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Devuelve el índice para el código hash en el arreglo de cubetas dado.
     */
    private int indexFor(int hash, int length) {
        return Math.abs(hash) % length;
    }

    /**
     * Maneja la operación put para claves nulas (almacenadas en el índice 0).
     */
    private V putForNullKey(V value) {
        for (Entry<K, V> entry = buckets[0]; entry != null; entry = entry.next) {
            if (entry.key == null) {
                V oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
        }
        addEntry(null, value, 0);
        return null;
    }

    /**
     * Maneja la operación get para claves nulas.
     */
    private V getForNullKey() {
        for (Entry<K, V> entry = buckets[0]; entry != null; entry = entry.next) {
            if (entry.key == null) {
                return entry.value;
            }
        }
        return null;
    }

    /**
     * Maneja la operación remove para claves nulas.
     */
    private V removeForNullKey() {
        Entry<K, V> prev = null;
        Entry<K, V> entry = buckets[0];

        while (entry != null) {
            if (entry.key == null) {
                if (prev == null) {
                    buckets[0] = entry.next;
                } else {
                    prev.next = entry.next;
                }
                size--;
                return entry.value;
            }
            prev = entry;
            entry = entry.next;
        }
        return null;
    }

    /**
     * Agrega una nueva entrada a la cubeta especificada.
     */
    private void addEntry(K key, V value, int bucketIndex) {
        Entry<K, V> entry = buckets[bucketIndex];
        buckets[bucketIndex] = new Entry<>(key, value, entry);
        size++;

        if (size >= threshold) {
            resize(2 * buckets.length);
        }
    }

    /**
     * Redimensiona la tabla hash a la nueva capacidad especificada.
     */
    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Entry<K, V>[] oldBuckets = buckets;
        buckets = (Entry<K, V>[]) new Entry[newCapacity];
        threshold = (int) (newCapacity * LOAD_FACTOR);
        size = 0;

        // Rehacer hash de todas las entradas
        for (Entry<K, V> bucket : oldBuckets) {
            for (Entry<K, V> entry = bucket; entry != null; entry = entry.next) {
                put(entry.key, entry.value);
            }
        }
    }

    /**
     * Devuelve una representación en cadena de este mapa.
     *
     * @return una representación en cadena de este mapa
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;

        for (Entry<K, V> bucket : buckets) {
            for (Entry<K, V> entry = bucket; entry != null; entry = entry.next) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(entry.key).append("=").append(entry.value);
                first = false;
            }
        }

        sb.append("}");
        return sb.toString();
    }
}
