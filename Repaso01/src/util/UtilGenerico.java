package util;

import java.util.ArrayList;

public class UtilGenerico {
    
    // Método 1: Retorna un ArrayList a partir de múltiples argumentos varargs
    public static <T> ArrayList<T> crearLista(T... elementos) {
        ArrayList<T> lista = new ArrayList<>();
        for (T elemento : elementos) {
            lista.add(elemento);
        }
        return lista;
    }

    // Método 2: Agrega múltiples elementos varargs a una lista existente
    public static <T> void agregarElementos(ArrayList<T> destino, T... elementos) {
        for (T elemento : elementos) {
            destino.add(elemento);
        }
    }
}