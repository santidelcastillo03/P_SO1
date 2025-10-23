/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datastructures;

import java.util.Objects;

/**
 * EnumMap implementa un mapa optimizado para claves de tipo enum.
 * Usa un array interno indexado por el ordinal del enum para acceso O(1).
 * Solo acepta claves de un tipo de enum específico definido en construcción.
 * 
 * @param <K> tipo enum de las claves
 * @param <V> tipo de los valores
 * @author santiagodelcastillo
 */
public class EnumMap<K extends Enum<K>, V> {
    
    /** Clase del tipo enum utilizado como clave. */
    private final Class<K> keyType;
    /** Array que almacena los valores indexados por el ordinal del enum. */
    private final Object[] values;
    /** Constantes del enum para validación y acceso. */
    private final K[] enumConstants;
    /** Número de entradas actualmente almacenadas (no nulas). */
    private int size;
    
    /**
     * Construye un EnumMap vacío para el tipo de enum especificado.
     * 
     * @param keyType clase del enum que se usará como clave
     */
    public EnumMap(Class<K> keyType) {
        this.keyType = Objects.requireNonNull(keyType, "El tipo de clave no puede ser nulo");
        this.enumConstants = keyType.getEnumConstants();
        
        if (enumConstants == null) {
            throw new IllegalArgumentException("El tipo proporcionado no es un enum");
        }
        
        this.values = new Object[enumConstants.length];
        this.size = 0;
    }
    
    /**
     * Inserta o actualiza un par clave-valor en el mapa.
     * Si la clave ya existe, actualiza su valor.
     * 
     * @param key clave enum a insertar (no puede ser null)
     * @param value valor a asociar con la clave
     * @return el valor anterior asociado con la clave, o null si no existía
     */
    public synchronized V put(K key, V value) {
        validateKey(key);
        
        int index = key.ordinal();
        @SuppressWarnings("unchecked")
        V oldValue = (V) values[index];
        
        values[index] = value;
        
        // Incrementar size solo si es una nueva entrada
        if (oldValue == null && value != null) {
            size++;
        } else if (oldValue != null && value == null) {
            size--;
        }
        
        return oldValue;
    }
    
    /**
     * Obtiene el valor asociado con la clave especificada.
     * 
     * @param key clave enum cuyo valor se desea obtener
     * @return el valor asociado con la clave, o null si no existe
     */
    public synchronized V get(K key) {
        if (key == null || key.getDeclaringClass() != keyType) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        V value = (V) values[key.ordinal()];
        return value;
    }
    
    /**
     * Elimina la entrada asociada con la clave especificada.
     * 
     * @param key clave enum cuya entrada se desea eliminar
     * @return el valor que estaba asociado con la clave, o null si no existía
     */
    public synchronized V remove(K key) {
        validateKey(key);
        
        int index = key.ordinal();
        @SuppressWarnings("unchecked")
        V oldValue = (V) values[index];
        
        if (oldValue != null) {
            values[index] = null;
            size--;
        }
        
        return oldValue;
    }
    
    /**
     * Verifica si el mapa contiene la clave especificada.
     * 
     * @param key clave enum cuya presencia se desea verificar
     * @return true si el mapa contiene la clave con un valor no nulo, false en caso contrario
     */
    public synchronized boolean containsKey(K key) {
        if (key == null || key.getDeclaringClass() != keyType) {
            return false;
        }
        return values[key.ordinal()] != null;
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
        for (int i = 0; i < values.length; i++) {
            values[i] = null;
        }
        size = 0;
    }
    
    /**
     * Obtiene todas las claves enum posibles para este mapa.
     * 
     * @return array con todas las constantes del enum
     */
    public K[] getKeys() {
        return enumConstants.clone();
    }
    
    /**
     * Valida que la clave sea del tipo correcto de enum.
     * 
     * @param key clave a validar
     * @throws NullPointerException si la clave es nula
     * @throws IllegalArgumentException si la clave no es del tipo esperado
     */
    private void validateKey(K key) {
        Objects.requireNonNull(key, "La clave no puede ser nula");
        if (key.getDeclaringClass() != keyType) {
            throw new IllegalArgumentException(
                "La clave debe ser del tipo " + keyType.getName()
            );
        }
    }
}
