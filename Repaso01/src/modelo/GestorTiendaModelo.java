package modelo;

import util.UtilGenerico;
import java.io.*;
import java.util.ArrayList;

public class GestorTiendaModelo {
    private ArrayList<Componente> inventario;
    private final String ARCHIVO = "inventario_pc.txt";

    public GestorTiendaModelo() {
        inventario = new ArrayList<>();
        cargarDatos(); // Carga o inicializa el archivo al arrancar
    }

    private void cargarDatos() {
        File file = new File(ARCHIVO);
        try {
            // Si el archivo no existe en el workspace, lo crea y le inyecta datos
            if (!file.exists()) {
                file.createNewFile();
                inicializarDatosPrueba();
                guardarDatos();
                return;
            }

            // Si existe, lee las líneas del .txt
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    String[] partes = linea.split(";");
                    if (partes.length == 4) {
                        Componente c = new Componente(partes[0], partes[1], 
                                Double.parseDouble(partes[2]), Integer.parseInt(partes[3]));
                        inventario.add(c);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        }
    }

    private void inicializarDatosPrueba() {
        Componente c1 = new Componente("Ryzen 5 5600X", "Procesador", 180.00, 10);
        Componente c2 = new Componente("RTX 3060 12GB", "Gráfica", 320.00, 5);
        Componente c3 = new Componente("RAM Corsair 16GB", "RAM", 45.00, 15);
        
        // Uso del método genérico para poblar la lista rápidamente
        UtilGenerico.agregarElementos(inventario, c1, c2, c3);
    }

    public void agregarComponente(Componente c) {
        UtilGenerico.agregarElementos(inventario, c);
        guardarDatos();
    }

    public ArrayList<Componente> getInventario() {
        return inventario;
    }

    public double[] procesarVenta(int indice, int cantidad) throws Exception {
        Componente comp = inventario.get(indice);
        if (cantidad > comp.getStock()) {
            throw new Exception("Stock insuficiente. Disponible: " + comp.getStock());
        }
        comp.reducirStock(cantidad);
        guardarDatos();

        double subtotal = comp.getPrecio() * cantidad;
        double iva = subtotal * 0.15; // 15% de IVA
        double total = subtotal + iva;
        
        return new double[]{subtotal, iva, total}; // Retorna arreglo numérico
    }

    private void guardarDatos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Componente c : inventario) {
                writer.println(c.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar archivo: " + e.getMessage());
        }
    }
}