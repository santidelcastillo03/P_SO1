/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datastructures;

import javax.swing.JOptionPane;

/**
 *
 * @author angel
 */
public class Lista {

    private NodoLista inicioLista, finalLista;
    String Lista = "";
    private int tamano;

    public Lista() {
        inicioLista = null;
        finalLista = null;
        tamano = 0;
    }

    // Método para verificar si la lista está vacía
    public boolean ListaVacia() {
        return inicioLista == null;
    }

    // Método para insertar un nombre en la lista
    public void Insertar(String nombre) {
        NodoLista nuevo_nodo = new NodoLista(nombre);
        nuevo_nodo.siguiente = null;

        if (ListaVacia()) {
            inicioLista = nuevo_nodo;
            finalLista = nuevo_nodo;
        } else {
            finalLista.siguiente = nuevo_nodo;
            finalLista = nuevo_nodo;
        }
        tamano++;
    }

    // Método para extraer un nombre de la lista
    public String Extraer() {
        if (!ListaVacia()) {
            String nombre = inicioLista.nombre;

            if (inicioLista == finalLista) {
                inicioLista = null;
                finalLista = null;
            } else {
                inicioLista = inicioLista.siguiente;
            }
            tamano--; 
            return nombre;
        } else {
            return null;
        }
    }

    // Método para mostrar el contenido de la lista
    public void MostrarContenido() {
        NodoLista recorrido = inicioLista;
        StringBuilder ListaInvertida = new StringBuilder();

        while (recorrido != null) {
            Lista += recorrido.nombre + " ";
            recorrido = recorrido.siguiente;
        }

        String[] cadena = Lista.split(" ");

        for (int i = cadena.length - 1; i >= 0; i--) {
            ListaInvertida.append(" ").append(cadena[i]);
        }

        JOptionPane.showMessageDialog(null, ListaInvertida.toString());
        Lista = "";
    }

    public String mostrarNombresIDInacion() {
        String nombres = "";
        NodoLista pointer = getInicioLista();
        for (int i = 0; i < this.getTamano(); i++) {
            nombres += pointer.getNombre() + "\n";
            pointer = pointer.getSiguiente();
        }
        return nombres;
    }

    public NodoLista getInicioLista() {
        return inicioLista;
    }

    public void setInicioLista(NodoLista inicioLista) {
        this.inicioLista = inicioLista;
    }

    public NodoLista getFinalLista() {
        return finalLista;
    }

    public void setFinalLista(NodoLista finalLista) {
        this.finalLista = finalLista;
    }

    public String getLista() {
        return Lista;
    }

    public void setLista(String Lista) {
        this.Lista = Lista;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }
}
