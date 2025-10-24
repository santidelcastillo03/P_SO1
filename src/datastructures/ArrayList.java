/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datastructures;

/**
 * Implementación de una lista dinámica similar a ArrayList de Java.
 * Esta clase proporciona una estructura de datos que puede crecer dinámicamente
 * y almacena elementos de tipo genérico E.
 *
 * @author santiagodelcastillo
 */
public class ArrayList<E> {
    // Array interno que almacena los elementos
    private Object[] elements;
    // Número actual de elementos en la lista
    private int size;
    // Capacidad inicial por defecto del array interno
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Constructor por defecto. Crea una lista con capacidad inicial de 10 elementos.
     */
    public ArrayList() {
        elements = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    /**
     * Constructor con capacidad inicial especificada.
     * @param initialCapacity La capacidad inicial del array interno
     * @throws IllegalArgumentException si initialCapacity es negativo
     */
    public ArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        elements = new Object[initialCapacity];
        size = 0;
    }

    /**
     * Agrega un elemento al final de la lista.
     * @param element El elemento a agregar
     */
    public void add(E element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
    }

    /**
     * Inserta un elemento en la posición especificada.
     * @param index La posición donde insertar el elemento
     * @param element El elemento a insertar
     * @throws IndexOutOfBoundsException si el índice está fuera de rango
     */
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    /**
     * Obtiene el elemento en la posición especificada.
     * @param index La posición del elemento a obtener
     * @return El elemento en la posición especificada
     * @throws IndexOutOfBoundsException si el índice está fuera de rango
     */
    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (E) elements[index];
    }

    /**
     * Elimina el elemento en la posición especificada.
     * @param index La posición del elemento a eliminar
     * @return El elemento que fue eliminado
     * @throws IndexOutOfBoundsException si el índice está fuera de rango
     */
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        E oldValue = (E) elements[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null; // Limpiar referencia para ayudar al GC
        return oldValue;
    }

    /**
     * Elimina la primera ocurrencia del elemento especificado.
     * @param o El elemento a eliminar
     * @return true si el elemento fue encontrado y eliminado, false en caso contrario
     */
    public boolean remove(Object o) {
        for (int i = 0; i < size; i++) {
            if (o.equals(elements[i])) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Devuelve el número de elementos en la lista.
     * @return El tamaño actual de la lista
     */
    public int size() {
        return size;
    }

    /**
     * Verifica si la lista está vacía.
     * @return true si la lista no contiene elementos, false en caso contrario
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Elimina todos los elementos de la lista.
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    /**
     * Verifica si la lista contiene el elemento especificado.
     * @param o El elemento a buscar
     * @return true si el elemento está presente, false en caso contrario
     */
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Devuelve el índice de la primera ocurrencia del elemento especificado.
     * @param o El elemento a buscar
     * @return El índice de la primera ocurrencia, o -1 si no se encuentra
     */
    public int indexOf(Object o) {
        for (int i = 0; i < size; i++) {
            if (o.equals(elements[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Devuelve el índice de la última ocurrencia del elemento especificado.
     * @param o El elemento a buscar
     * @return El índice de la última ocurrencia, o -1 si no se encuentra
     */
    public int lastIndexOf(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            if (o.equals(elements[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Asegura que el array interno tenga suficiente capacidad para el número mínimo de elementos especificado.
     * Si es necesario, redimensiona el array duplicando su tamaño.
     * @param minCapacity La capacidad mínima requerida
     */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            Object[] newElements = new Object[newCapacity];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
        }
    }

    /**
     * Devuelve una representación en cadena de la lista.
     * @return Una cadena que representa el contenido de la lista
     */
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
