/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datastructures;

/**
 * Clase que representa un nodo en una lista enlazada.
 * Cada nodo contiene un elemento de datos y una referencia al siguiente nodo.
 *
 * @author santiagodelcastillo
 */
public class NodeLinkedList<E> {
    // El elemento de datos almacenado en este nodo
    private E data;
    // Referencia al siguiente nodo en la lista
    private NodeLinkedList<E> next;

    /**
     * Constructor por defecto. Crea un nodo sin datos ni siguiente nodo.
     */
    public NodeLinkedList() {
        this.data = null;
        this.next = null;
    }

    /**
     * Constructor que crea un nodo con datos especificados.
     * @param data Los datos a almacenar en este nodo
     */
    public NodeLinkedList(E data) {
        this.data = data;
        this.next = null;
    }

    /**
     * Constructor que crea un nodo con datos y siguiente nodo especificados.
     * @param data Los datos a almacenar en este nodo
     * @param next El siguiente nodo en la lista
     */
    public NodeLinkedList(E data, NodeLinkedList<E> next) {
        this.data = data;
        this.next = next;
    }

    /**
     * Obtiene los datos almacenados en este nodo.
     * @return Los datos del nodo
     */
    public E getData() {
        return data;
    }

    /**
     * Establece los datos para este nodo.
     * @param data Los nuevos datos para el nodo
     */
    public void setData(E data) {
        this.data = data;
    }

    /**
     * Obtiene el siguiente nodo en la lista.
     * @return El siguiente nodo, o null si es el último
     */
    public NodeLinkedList<E> getNext() {
        return next;
    }

    /**
     * Establece el siguiente nodo en la lista.
     * @param next El nuevo siguiente nodo
     */
    public void setNext(NodeLinkedList<E> next) {
        this.next = next;
    }

    /**
     * Devuelve una representación en cadena del nodo.
     * @return Una cadena que representa los datos del nodo
     */
    @Override
    public String toString() {
        return data != null ? data.toString() : "null";
    }
}
