package delegado;

import modelo.Vehiculo;
import modelo.GestorParqueaderoModelo;
import util.UtilGenerico;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VentanaParqueadero extends JFrame {
    private GestorParqueaderoModelo modelo; 
    
    private JTable tabla;
    private DefaultTableModel dtm;
    private JTextField txtPlaca, txtHoraIngreso;
    private JComboBox<String> cbTipo;
    
    // Nueva paleta: Fondo claro y acentos en azul oscuro
    private Color bgClaro = Color.WHITE;
    private Color azulOscuro = new Color(10, 25, 47);

    public VentanaParqueadero() {
        modelo = new GestorParqueaderoModelo(); 
        configurarVentana();
        inicializarUI();
        refrescarTabla();
    }

    private void configurarVentana() {
        setTitle("ParkControl v2.0 - Gestión de Ingresos");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgClaro);
        setLayout(new BorderLayout(10, 10)); // Espaciado entre paneles
    }

    private void inicializarUI() {
        // PANEL NORTE (Formulario de Ingreso)
        JPanel panelNorte = new JPanel(new GridLayout(2, 4, 10, 5));
        panelNorte.setBackground(bgClaro);
        
        TitledBorder bordeNorte = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(azulOscuro, 2), "Registro de Vehículos"
        );
        bordeNorte.setTitleColor(azulOscuro);
        panelNorte.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 5, 10), bordeNorte
        ));

        txtPlaca = crearTextField();
        txtHoraIngreso = crearTextField();
        
        // Uso de método genérico varargs
        String[] tipos = UtilGenerico.crearLista("Auto", "Moto").toArray(new String[0]);
        cbTipo = new JComboBox<>(tipos);
        cbTipo.setBackground(bgClaro);
        cbTipo.setForeground(azulOscuro);
        
        JButton btnIngresar = crearBoton("Registrar Ingreso");

        panelNorte.add(crearLabel("Placa:"));
        panelNorte.add(crearLabel("Tipo Vehículo:"));
        panelNorte.add(crearLabel("Hora Ingreso (ej. 8):"));
        panelNorte.add(new JLabel("")); // Espaciador
        
        panelNorte.add(txtPlaca);
        panelNorte.add(cbTipo);
        panelNorte.add(txtHoraIngreso);
        panelNorte.add(btnIngresar);
        
        add(panelNorte, BorderLayout.NORTH);

        // PANEL CENTRAL (Tabla de Historial)
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(bgClaro);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        dtm = new DefaultTableModel(new String[]{"Placa", "Tipo", "H. Ingreso", "H. Salida (Estado)"}, 0);
        tabla = new JTable(dtm);
        tabla.setBackground(bgClaro);
        tabla.setForeground(azulOscuro);
        tabla.setGridColor(azulOscuro);
        
        // Estilo de la cabecera de la tabla
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(azulOscuro);
        header.setForeground(bgClaro);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(bgClaro);
        scroll.setBorder(BorderFactory.createLineBorder(azulOscuro, 1));
        panelCentral.add(scroll, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // PANEL SUR (Cobro y Salida)
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.setBackground(bgClaro);
        panelSur.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        JButton btnSalida = crearBoton("Procesar Salida y Cobrar");
        panelSur.add(btnSalida);
        add(panelSur, BorderLayout.SOUTH);

        // EVENTOS (Controlador embebido)
        btnIngresar.addActionListener((ActionEvent e) -> {
            try {
                if(txtPlaca.getText().trim().isEmpty() || txtHoraIngreso.getText().trim().isEmpty()){
                    throw new IllegalArgumentException("Llene todos los campos.");
                }
                Vehiculo v = new Vehiculo(
                    txtPlaca.getText().toUpperCase(),
                    cbTipo.getSelectedItem().toString(),
                    Integer.parseInt(txtHoraIngreso.getText())
                );
                modelo.registrarIngreso(v);
                refrescarTabla();
                txtPlaca.setText(""); txtHoraIngreso.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnSalida.addActionListener((ActionEvent e) -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un vehículo de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String input = JOptionPane.showInputDialog(this, "Ingrese Hora de Salida (Formato 24h):");
            if (input == null || input.trim().isEmpty()) return;

            try {
                int horaSalida = Integer.parseInt(input);
                double totalPagar = modelo.registrarSalida(fila, horaSalida); 
                
                String msj = String.format("SALIDA PROCESADA EXITOSAMENTE\n\nTotal a Pagar: $%.2f", totalPagar);
                JOptionPane.showMessageDialog(this, msj, "Ticket de Cobro", JOptionPane.INFORMATION_MESSAGE);
                refrescarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Cobro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Métodos auxiliares visuales actualizados
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(azulOscuro);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        return lbl;
    }

    private JTextField crearTextField() {
        JTextField txt = new JTextField();
        txt.setBackground(bgClaro);
        txt.setForeground(azulOscuro);
        txt.setBorder(BorderFactory.createLineBorder(azulOscuro, 1));
        return txt;
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(azulOscuro);
        btn.setForeground(bgClaro);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        return btn;
    }

    private void refrescarTabla() {
        dtm.setRowCount(0);
        for (Vehiculo v : modelo.getHistorial()) {
            String estado = (v.getHoraSalida() == 0) ? "En Parqueadero" : String.valueOf(v.getHoraSalida());
            dtm.addRow(new Object[]{v.getPlaca(), v.getTipo(), v.getHoraIngreso(), estado});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaParqueadero().setVisible(true));
    }
}