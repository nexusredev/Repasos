package util;

import java.util.ArrayList;

public class UtilGenerico {
    public static <T> ArrayList<T> crearLista(T... elementos) {
        ArrayList<T> lista = new ArrayList<>();
        for (T elemento : elementos) {
            lista.add(elemento);
        }
        return lista;
    }

    public static <T> void agregarElementos(ArrayList<T> destino, T... elementos) {
        for (T elemento : elementos) {
            destino.add(elemento);
        }
    }
}