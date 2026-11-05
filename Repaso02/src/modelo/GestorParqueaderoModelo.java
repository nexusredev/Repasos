package modelo;

import util.UtilGenerico;
import java.io.*;
import java.util.ArrayList;

public class GestorParqueaderoModelo {
    private ArrayList<Vehiculo> historial;
    private final String ARCHIVO = "historial_parqueo.txt";

    public GestorParqueaderoModelo() {
        historial = new ArrayList<>();
        cargarDatos();
    }

    private void cargarDatos() {
        File file = new File(ARCHIVO);
        try {
            if (!file.exists()) {
                file.createNewFile();
                // Inicializa con datos genéricos usando varargs
                Vehiculo v1 = new Vehiculo("PAB-1234", "Auto", 8);
                Vehiculo v2 = new Vehiculo("MIO-567", "Moto", 9);
                UtilGenerico.agregarElementos(historial, v1, v2);
                guardarDatos();
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    String[] partes = linea.split(";");
                    if (partes.length == 4) {
                        Vehiculo v = new Vehiculo(partes[0], partes[1], 
                                Integer.parseInt(partes[2]), Integer.parseInt(partes[3]));
                        historial.add(v);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        }
    }

    public void registrarIngreso(Vehiculo v) {
        UtilGenerico.agregarElementos(historial, v);
        guardarDatos();
    }

    public ArrayList<Vehiculo> getHistorial() {
        return historial;
    }

    // Calcula tarifa basado en horas enteras. Auto: $1.50/h | Moto: $0.75/h
    public double registrarSalida(int indice, int horaSalida) throws Exception {
        Vehiculo v = historial.get(indice);
        if (v.getHoraSalida() > 0) {
            throw new Exception("El vehículo ya fue cobrado y despachado.");
        }
        if (horaSalida <= v.getHoraIngreso()) {
            throw new Exception("La hora de salida debe ser mayor a la de ingreso.");
        }

        v.setHoraSalida(horaSalida);
        int horas = horaSalida - v.getHoraIngreso();
        
        double tarifa = v.getTipo().equals("Auto") ? 1.50 : 0.75;
        double total = horas * tarifa;
        
        guardarDatos();
        return total;
    }

    private void guardarDatos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Vehiculo v : historial) {
                writer.println(v.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar archivo: " + e.getMessage());
        }
    }
}