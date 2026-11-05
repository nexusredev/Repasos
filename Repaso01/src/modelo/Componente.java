package modelo;

public class Componente {
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;

    public Componente(String nombre, String categoria, double precio, int stock) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }

    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    
    public void reducirStock(int cantidad) { 
        this.stock -= cantidad; 
    }
    
    // Formato CSV para guardar en el archivo de texto
    public String toCSV() {
        return nombre + ";" + categoria + ";" + precio + ";" + stock;
    }
}