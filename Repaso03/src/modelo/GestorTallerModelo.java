package modelo;

import util.UtilGenerico;
import java.io.*;
import java.util.ArrayList;

public class GestorTallerModelo {
    private ArrayList<OrdenTrabajo> ordenes;
    private final String ARCHIVO = "historial_taller.txt";

    public GestorTallerModelo() {
        ordenes = new ArrayList<>();
        cargarDatos();
    }

    private void cargarDatos() {
        File file = new File(ARCHIVO);
        try {
            if (!file.exists()) {
                file.createNewFile();
                // Inicializa con datos de prueba
                OrdenTrabajo o1 = new OrdenTrabajo("Luis Pérez", "Laptop", "Pantalla rota", 25.0);
                OrdenTrabajo o2 = new OrdenTrabajo("Ana Gómez", "Celular", "No enciende", 15.0);
                UtilGenerico.agregarElementos(ordenes, o1, o2);
                guardarDatos();
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    String[] p = linea.split(";");
                    if (p.length == 6) {
                        OrdenTrabajo o = new OrdenTrabajo(p[0], p[1], p[2], 
                                Double.parseDouble(p[3]), Double.parseDouble(p[4]), p[5]);
                        ordenes.add(o);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        }
    }

    public void agregarOrden(OrdenTrabajo orden) {
        UtilGenerico.agregarElementos(ordenes, orden);
        guardarDatos();
    }

    public ArrayList<OrdenTrabajo> getOrdenes() { return ordenes; }

    public void cambiarEstado(int indice, String nuevoEstado) throws Exception {
        OrdenTrabajo o = ordenes.get(indice);
        if (o.getEstado().equals("Entregado")) throw new Exception("La orden ya está cerrada (Entregado).");
        o.setEstado(nuevoEstado);
        guardarDatos();
    }

    public void agregarRepuestos(int indice, double costoExtra) throws Exception {
        OrdenTrabajo o = ordenes.get(indice);
        if (o.getEstado().equals("Entregado")) throw new Exception("No se pueden sumar repuestos a una orden cerrada.");
        o.agregarCostoRepuestos(costoExtra);
        guardarDatos();
    }

    public double entregarEquipo(int indice) throws Exception {
        OrdenTrabajo o = ordenes.get(indice);
        if (o.getEstado().equals("Entregado")) throw new Exception("El equipo ya fue entregado y cobrado.");
        o.setEstado("Entregado");
        guardarDatos();
        return o.getCostoTotal();
    }

    private void guardarDatos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (OrdenTrabajo o : ordenes) {
                writer.println(o.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar archivo: " + e.getMessage());
        }
    }
}