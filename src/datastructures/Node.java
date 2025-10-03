/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


// Paquete de estructuras de datos personalizadas
package datastructures;

/**
 * Node representa un solo elemento en una lista enlazada simple utilizada por la cola.
 * @param <T> tipo de valor almacenado en el nodo
 */

// Clase de nodo genérico para lista enlazada simple
class Node<T> {


    /** Valor almacenado en el nodo (inmutable). */
    private final T value;
    /** Referencia al siguiente nodo en la lista, o null si es el último. */
    private Node<T> next;


    /**
     * Crea un nodo que almacena el valor proporcionado.
     * @param value elemento asociado al nodo
     */
    Node(T value) {
        this.value = value;
    }


    /**
     * Devuelve el valor almacenado en este nodo.
     * @return elemento guardado
     */
    T getValue() {
        return value;
    }


    /**
     * Obtiene el nodo referenciado como siguiente.
     * @return siguiente nodo en la lista o null si no hay más
     */
    Node<T> getNext() {
        return next;
    }


    /**
     * Actualiza la referencia al siguiente nodo.
     * @param next nodo que debe seguir a este nodo
     */
    void setNext(Node<T> next) {
        this.next = next;
    }
}
