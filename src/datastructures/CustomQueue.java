/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package datastructures;

import java.util.Objects;

/**
 * CustomQueue implementa una cola FIFO segura para hilos, respaldada por una lista enlazada simple.
 * @param <T> tipo de elementos que maneja la cola
 */
public class CustomQueue<T> {

    /** Referencia al primer nodo de la cola (cabeza). */
    private Node<T> head;
    /** Referencia al último nodo de la cola (cola). */
    private Node<T> tail;
    /** Número de elementos almacenados actualmente. */
    private int size;

    /**
     * Inserta el valor proporcionado al final de la cola.
     * Opera en tiempo constante gracias a la referencia a tail.
     * @param value elemento a encolar; se permiten valores null para simular colas reales
     */
    public synchronized void enqueue(T value) {
        Node<T> newNode = new Node<>(value);
        if (isEmptyInternal()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
    }

    /**
     * Elimina y retorna el elemento al frente de la cola, o null si está vacía.
     * @return elemento en la cabeza o null si no hay elementos
     */
    public synchronized T dequeue() {
        if (isEmptyInternal()) {
            return null;
        }
        Node<T> removed = head;
        head = removed.getNext();
        if (head == null) {
            tail = null;
        }
        size--;
        removed.setNext(null); // Ayuda al recolector de basura
        return removed.getValue();
    }

    /**
     * Devuelve el elemento al frente de la cola sin eliminarlo.
     * @return elemento actual en la cabeza o null si la cola está vacía
     */
    public synchronized T peek() {
        if (isEmptyInternal()) {
            return null;
        }
        return head.getValue();
    }

    /**
     * Elimina la primera ocurrencia del valor proporcionado de la cola.
     * @param value elemento que se debe eliminar
     * @return true si se eliminó un elemento, false en caso contrario
     */
    public synchronized boolean remove(T value) {
        if (isEmptyInternal()) {
            return false;
        }
        if (Objects.equals(head.getValue(), value)) {
            dequeue();
            return true;
        }
        Node<T> previous = head;
        Node<T> current = head.getNext();
        while (current != null) {
            if (Objects.equals(current.getValue(), value)) {
                previous.setNext(current.getNext());
                if (current == tail) {
                    tail = previous;
                }
                size--;
                current.setNext(null); // Ayuda al recolector de basura
                return true;
            }
            previous = current;
            current = current.getNext();
        }
        return false;
    }

    /**
     * Indica si la cola está vacía.
     * @return true si la cola no contiene elementos, false en caso contrario
     */
    public synchronized boolean isEmpty() {
        return isEmptyInternal();
    }

    /**
     * Devuelve el número de elementos almacenados actualmente en la cola.
     * @return cantidad de elementos
     */
    public synchronized int size() {
        return size;
    }

    /**
     * Vacía el contenido de la cola eliminando todas las referencias almacenadas.
     */
    public synchronized void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Método auxiliar interno para verificar si la cola está vacía sin relockear.
     * @return true si el tamaño es 0
     */
    private boolean isEmptyInternal() {
        return size == 0;
    }

    /**
     * Obtiene una copia ordenada de los elementos almacenados en la cola sin usar colecciones externas.
     * @return arreglo con la instantánea de los elementos en orden FIFO
     */
    public synchronized Object[] getAllProcesses() {
        Object[] snapshot = new Object[size];
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            snapshot[index++] = current.getValue();
            current = current.getNext();
        }
        return snapshot;
    }
}
