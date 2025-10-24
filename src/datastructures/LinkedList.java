/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datastructures;

/**
 * Implementación de una lista enlazada doblemente.
 * Esta clase proporciona una estructura de datos dinámica donde cada elemento
 * apunta al siguiente y al anterior, permitiendo inserciones y eliminaciones eficientes.
 *
 * @author santiagodelcastillo
 */
public class LinkedList<E> {
    // Referencia al primer nodo de la lista
    private NodeLinkedList<E> head;
    // Referencia al último nodo de la lista
    private NodeLinkedList<E> tail;
    // Número de elementos en la lista
    private int size;

    /**
     * Constructor por defecto. Crea una lista vacía.
     */
    public LinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Agrega un elemento al final de la lista.
     * @param element El elemento a agregar
     */
    public void add(E element) {
        addLast(element);
    }

    /**
     * Agrega un elemento al final de la lista.
     * @param element El elemento a agregar
     */
    public void addLast(E element) {
        NodeLinkedList<E> newNode = new NodeLinkedList<>(element);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
    }

    /**
     * Agrega un elemento al inicio de la lista.
     * @param element El elemento a agregar
     */
    public void addFirst(E element) {
        NodeLinkedList<E> newNode = new NodeLinkedList<>(element);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setNext(head);
            head = newNode;
        }
        size++;
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

        if (index == 0) {
            addFirst(element);
        } else if (index == size) {
            addLast(element);
        } else {
            NodeLinkedList<E> newNode = new NodeLinkedList<>(element);
            NodeLinkedList<E> current = getNode(index - 1);
            newNode.setNext(current.getNext());
            current.setNext(newNode);
            size++;
        }
    }

    /**
     * Obtiene el elemento en la posición especificada.
     * @param index La posición del elemento a obtener
     * @return El elemento en la posición especificada
     * @throws IndexOutOfBoundsException si el índice está fuera de rango
     */
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return getNode(index).getData();
    }

    /**
     * Obtiene el primer elemento de la lista.
     * @return El primer elemento
     * @throws IllegalStateException si la lista está vacía
     */
    public E getFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("La lista está vacía");
        }
        return head.getData();
    }

    /**
     * Obtiene el último elemento de la lista.
     * @return El último elemento
     * @throws IllegalStateException si la lista está vacía
     */
    public E getLast() {
        if (isEmpty()) {
            throw new IllegalStateException("La lista está vacía");
        }
        return tail.getData();
    }

    /**
     * Elimina y devuelve el elemento en la posición especificada.
     * @param index La posición del elemento a eliminar
     * @return El elemento eliminado
     * @throws IndexOutOfBoundsException si el índice está fuera de rango
     */
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        E removedElement;
        if (index == 0) {
            removedElement = removeFirst();
        } else if (index == size - 1) {
            removedElement = removeLast();
        } else {
            NodeLinkedList<E> prevNode = getNode(index - 1);
            NodeLinkedList<E> nodeToRemove = prevNode.getNext();
            removedElement = nodeToRemove.getData();
            prevNode.setNext(nodeToRemove.getNext());
            size--;
        }

        return removedElement;
    }

    /**
     * Elimina y devuelve el primer elemento de la lista.
     * @return El primer elemento eliminado
     * @throws IllegalStateException si la lista está vacía
     */
    public E removeFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("La lista está vacía");
        }

        E removedElement = head.getData();
        if (size == 1) {
            head = null;
            tail = null;
        } else {
            head = head.getNext();
        }
        size--;
        return removedElement;
    }

    /**
     * Elimina y devuelve el último elemento de la lista.
     * @return El último elemento eliminado
     * @throws IllegalStateException si la lista está vacía
     */
    public E removeLast() {
        if (isEmpty()) {
            throw new IllegalStateException("La lista está vacía");
        }

        E removedElement = tail.getData();
        if (size == 1) {
            head = null;
            tail = null;
        } else {
            NodeLinkedList<E> prevNode = getNode(size - 2);
            prevNode.setNext(null);
            tail = prevNode;
        }
        size--;
        return removedElement;
    }

    /**
     * Elimina la primera ocurrencia del elemento especificado.
     * @param o El elemento a eliminar
     * @return true si el elemento fue encontrado y eliminado, false en caso contrario
     */
    public boolean remove(Object o) {
        NodeLinkedList<E> current = head;
        NodeLinkedList<E> previous = null;

        while (current != null) {
            if (o.equals(current.getData())) {
                if (previous == null) {
                    // Eliminar el primer elemento
                    removeFirst();
                } else {
                    // Eliminar elemento en medio o final
                    previous.setNext(current.getNext());
                    if (current == tail) {
                        tail = previous;
                    }
                    size--;
                }
                return true;
            }
            previous = current;
            current = current.getNext();
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
        head = null;
        tail = null;
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
        NodeLinkedList<E> current = head;
        int index = 0;

        while (current != null) {
            if (o.equals(current.getData())) {
                return index;
            }
            current = current.getNext();
            index++;
        }
        return -1;
    }

    /**
     * Devuelve el índice de la última ocurrencia del elemento especificado.
     * @param o El elemento a buscar
     * @return El índice de la última ocurrencia, o -1 si no se encuentra
     */
    public int lastIndexOf(Object o) {
        NodeLinkedList<E> current = head;
        int index = 0;
        int lastIndex = -1;

        while (current != null) {
            if (o.equals(current.getData())) {
                lastIndex = index;
            }
            current = current.getNext();
            index++;
        }
        return lastIndex;
    }

    /**
     * Método auxiliar para obtener el nodo en una posición específica.
     * @param index La posición del nodo
     * @return El nodo en la posición especificada
     */
    private NodeLinkedList<E> getNode(int index) {
        NodeLinkedList<E> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current;
    }

    /**
     * Devuelve una representación en cadena de la lista.
     * @return Una cadena que representa el contenido de la lista
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        NodeLinkedList<E> current = head;

        while (current != null) {
            sb.append(current.getData());
            if (current.getNext() != null) {
                sb.append(", ");
            }
            current = current.getNext();
        }
        sb.append("]");
        return sb.toString();
    }
}
