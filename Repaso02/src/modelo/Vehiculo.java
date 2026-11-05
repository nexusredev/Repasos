package modelo;

public class Vehiculo {
    private String placa;
    private String tipo;
    private int horaIngreso;
    private int horaSalida;

    public Vehiculo(String placa, String tipo, int horaIngreso) {
        this.placa = placa;
        this.tipo = tipo;
        this.horaIngreso = horaIngreso;
        this.horaSalida = 0; // 0 significa que aún está en el parqueadero
    }
    
    // Constructor completo para cuando se lee del archivo
    public Vehiculo(String placa, String tipo, int horaIngreso, int horaSalida) {
        this.placa = placa;
        this.tipo = tipo;
        this.horaIngreso = horaIngreso;
        this.horaSalida = horaSalida;
    }

    public String getPlaca() { return placa; }
    public String getTipo() { return tipo; }
    public int getHoraIngreso() { return horaIngreso; }
    public int getHoraSalida() { return horaSalida; }

    public void setHoraSalida(int horaSalida) { this.horaSalida = horaSalida; }

    public String toCSV() {
        return placa + ";" + tipo + ";" + horaIngreso + ";" + horaSalida;
    }
}