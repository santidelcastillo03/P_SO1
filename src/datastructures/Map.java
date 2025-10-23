/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datastructures;

import java.util.Objects;

/**
 * Map implementa una estructura de datos clave-valor genérica usando una lista enlazada.
 * Permite asociar claves únicas con valores y realizar búsquedas, inserciones y eliminaciones.
 * 
 * @param <K> tipo de las claves
 * @param <V> tipo de los valores
 * @author santiagodelcastillo
 */
public class Map<K, V> {
    
    /**
     * Entry representa un par clave-valor en el mapa.
     */
    private static class Entry<K, V> {
        private final K key;
        private V value;
        private Entry<K, V> next;
        
        /**
         * Construye una nueva entrada con la clave y valor especificados.
         * @param key clave de la entrada
         * @param value valor asociado a la clave
         */
        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
        
        public K getKey() {
            return key;
        }
        
        public V getValue() {
            return value;
        }
        
        public void setValue(V value) {
            this.value = value;
        }
        
        public Entry<K, V> getNext() {
            return next;
        }
        
        public void setNext(Entry<K, V> next) {
            this.next = next;
        }
    }
    
    /** Cabeza de la lista enlazada de entradas. */
    private Entry<K, V> head;
    /** Número de elementos almacenados en el mapa. */
    private int size;
    
    /**
     * Construye un mapa vacío.
     */
    public Map() {
        this.head = null;
        this.size = 0;
    }
    
    /**
     * Inserta o actualiza un par clave-valor en el mapa.
     * Si la clave ya existe, actualiza su valor.
     * 
     * @param key clave a insertar (no puede ser null)
     * @param value valor a asociar con la clave
     * @return el valor anterior asociado con la clave, o null si no existía
     */
    public synchronized V put(K key, V value) {
        Objects.requireNonNull(key, "La clave no puede ser nula");
        
        // Buscar si la clave ya existe
        Entry<K, V> current = head;
        while (current != null) {
            if (Objects.equals(current.getKey(), key)) {
                V oldValue = current.getValue();
                current.setValue(value);
                return oldValue;
            }
            current = current.getNext();
        }
        
        // Si no existe, agregar nueva entrada al inicio
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.setNext(head);
        head = newEntry;
        size++;
        return null;
    }
    
    /**
     * Obtiene el valor asociado con la clave especificada.
     * 
     * @param key clave cuyo valor se desea obtener
     * @return el valor asociado con la clave, o null si no existe
     */
    public synchronized V get(K key) {
        if (key == null) {
            return null;
        }
        
        Entry<K, V> current = head;
        while (current != null) {
            if (Objects.equals(current.getKey(), key)) {
                return current.getValue();
            }
            current = current.getNext();
        }
        return null;
    }
    
    /**
     * Elimina la entrada asociada con la clave especificada.
     * 
     * @param key clave cuya entrada se desea eliminar
     * @return el valor que estaba asociado con la clave, o null si no existía
     */
    public synchronized V remove(K key) {
        if (key == null || head == null) {
            return null;
        }
        
        // Caso especial: la clave está en la cabeza
        if (Objects.equals(head.getKey(), key)) {
            V value = head.getValue();
            head = head.getNext();
            size--;
            return value;
        }
        
        // Buscar en el resto de la lista
        Entry<K, V> previous = head;
        Entry<K, V> current = head.getNext();
        
        while (current != null) {
            if (Objects.equals(current.getKey(), key)) {
                V value = current.getValue();
                previous.setNext(current.getNext());
                size--;
                return value;
            }
            previous = current;
            current = current.getNext();
        }
        
        return null;
    }
    
    /**
     * Verifica si el mapa contiene la clave especificada.
     * 
     * @param key clave cuya presencia se desea verificar
     * @return true si el mapa contiene la clave, false en caso contrario
     */
    public synchronized boolean containsKey(K key) {
        if (key == null) {
            return false;
        }
        
        Entry<K, V> current = head;
        while (current != null) {
            if (Objects.equals(current.getKey(), key)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }
    
    /**
     * Verifica si el mapa está vacío.
     * 
     * @return true si el mapa no contiene ninguna entrada, false en caso contrario
     */
    public synchronized boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Obtiene el número de entradas en el mapa.
     * 
     * @return cantidad de pares clave-valor almacenados
     */
    public synchronized int size() {
        return size;
    }
    
    /**
     * Elimina todas las entradas del mapa.
     */
    public synchronized void clear() {
        head = null;
        size = 0;
    }
}
