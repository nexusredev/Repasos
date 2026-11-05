package modelo;

public class OrdenTrabajo {
    private String cliente;
    private String dispositivo;
    private String diagnostico;
    private double costoBase;
    private double costoRepuestos;
    private String estado; // Pendiente, En Proceso, Entregado

    // Constructor para órdenes nuevas
    public OrdenTrabajo(String cliente, String dispositivo, String diagnostico, double costoBase) {
        this.cliente = cliente;
        this.dispositivo = dispositivo;
        this.diagnostico = diagnostico;
        this.costoBase = costoBase;
        this.costoRepuestos = 0.0;
        this.estado = "Pendiente";
    }

    // Constructor completo para carga desde archivo
    public OrdenTrabajo(String cliente, String dispositivo, String diagnostico, double costoBase, double costoRepuestos, String estado) {
        this.cliente = cliente;
        this.dispositivo = dispositivo;
        this.diagnostico = diagnostico;
        this.costoBase = costoBase;
        this.costoRepuestos = costoRepuestos;
        this.estado = estado;
    }

    public String getCliente() { return cliente; }
    public String getDispositivo() { return dispositivo; }
    public String getDiagnostico() { return diagnostico; }
    public double getCostoBase() { return costoBase; }
    public double getCostoRepuestos() { return costoRepuestos; }
    public String getEstado() { return estado; }
    
    public double getCostoTotal() { return costoBase + costoRepuestos; }

    public void setEstado(String estado) { this.estado = estado; }
    public void agregarCostoRepuestos(double costo) { this.costoRepuestos += costo; }

    public String toCSV() {
        return cliente + ";" + dispositivo + ";" + diagnostico + ";" + costoBase + ";" + costoRepuestos + ";" + estado;
    }
}