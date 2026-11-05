package delegado;

import modelo.OrdenTrabajo;
import modelo.GestorTallerModelo;
import util.UtilGenerico;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VentanaTaller extends JFrame {
    private GestorTallerModelo modelo;

    private JTable tabla;
    private DefaultTableModel dtm;
    private JTextField txtCliente, txtDiagnostico, txtCostoBase;
    private JComboBox<String> cbDispositivo;
    
    // Paleta de diseño FixTech
    private Color bgClaro = Color.WHITE;
    private Color azulOscuro = new Color(10, 25, 47);

    public VentanaTaller() {
        modelo = new GestorTallerModelo();
        configurarVentana();
        inicializarUI();
        refrescarTabla();
    }

    private void configurarVentana() {
        setTitle("FixTech v2.0 - Gestión de Reparaciones");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgClaro);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarUI() {
        // PANEL NORTE (Registro)
        JPanel panelNorte = new JPanel(new GridLayout(2, 5, 10, 5));
        panelNorte.setBackground(bgClaro);
        
        TitledBorder borde = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(azulOscuro, 2), "Recepción de Equipo");
        borde.setTitleColor(azulOscuro);
        panelNorte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 5, 10), borde));

        txtCliente = crearTextField();
        txtDiagnostico = crearTextField();
        txtCostoBase = crearTextField();
        
        // Uso genérico
        String[] equipos = UtilGenerico.crearLista("Laptop", "Desktop PC", "Celular", "Consola").toArray(new String[0]);
        cbDispositivo = new JComboBox<>(equipos);
        cbDispositivo.setBackground(bgClaro);
        cbDispositivo.setForeground(azulOscuro);
        
        JButton btnRegistrar = crearBoton("Registrar Orden");

        panelNorte.add(crearLabel("Cliente:"));
        panelNorte.add(crearLabel("Dispositivo:"));
        panelNorte.add(crearLabel("Diagnóstico Inicial:"));
        panelNorte.add(crearLabel("Costo Base (Mano de obra):"));
        panelNorte.add(new JLabel("")); // Espaciador
        
        panelNorte.add(txtCliente);
        panelNorte.add(cbDispositivo);
        panelNorte.add(txtDiagnostico);
        panelNorte.add(txtCostoBase);
        panelNorte.add(btnRegistrar);

        add(panelNorte, BorderLayout.NORTH);

        // PANEL CENTRAL (Tabla)
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(bgClaro);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        dtm = new DefaultTableModel(new String[]{"Cliente", "Equipo", "Diagnóstico", "Estado", "Total ($)"}, 0);
        tabla = new JTable(dtm);
        tabla.setBackground(bgClaro);
        tabla.setForeground(azulOscuro);
        tabla.setGridColor(azulOscuro);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(azulOscuro);
        header.setForeground(bgClaro);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(bgClaro);
        scroll.setBorder(BorderFactory.createLineBorder(azulOscuro, 1));
        panelCentral.add(scroll, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // PANEL SUR (Acciones)
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelSur.setBackground(bgClaro);
        
        JButton btnEstado = crearBoton("Pasar a 'En Proceso'");
        JButton btnRepuesto = crearBoton("Asignar Repuestos (+)");
        JButton btnEntregar = crearBoton("Cobrar y Entregar");
        
        panelSur.add(btnEstado);
        panelSur.add(btnRepuesto);
        panelSur.add(btnEntregar);
        add(panelSur, BorderLayout.SOUTH);

        // ACTION LISTENERS (Controlador)
        btnRegistrar.addActionListener((ActionEvent e) -> {
            try {
                if (txtCliente.getText().isEmpty() || txtCostoBase.getText().isEmpty()) {
                    throw new Exception("Llene los campos obligatorios.");
                }
                OrdenTrabajo orden = new OrdenTrabajo(
                        txtCliente.getText(),
                        cbDispositivo.getSelectedItem().toString(),
                        txtDiagnostico.getText(),
                        Double.parseDouble(txtCostoBase.getText())
                );
                modelo.agregarOrden(orden);
                refrescarTabla();
                txtCliente.setText(""); txtDiagnostico.setText(""); txtCostoBase.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEstado.addActionListener((ActionEvent e) -> {
            int fila = validarSeleccion();
            if (fila == -1) return;
            try {
                modelo.cambiarEstado(fila, "En Proceso");
                refrescarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnRepuesto.addActionListener((ActionEvent e) -> {
            int fila = validarSeleccion();
            if (fila == -1) return;
            String input = JOptionPane.showInputDialog(this, "Costo de los repuestos ($):");
            if (input != null && !input.isEmpty()) {
                try {
                    modelo.agregarRepuestos(fila, Double.parseDouble(input));
                    refrescarTabla();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        });

        btnEntregar.addActionListener((ActionEvent e) -> {
            int fila = validarSeleccion();
            if (fila == -1) return;
            try {
                double total = modelo.entregarEquipo(fila);
                String msj = String.format("ORDEN FINALIZADA\n\nTotal a cobrar al cliente: $%.2f", total);
                JOptionPane.showMessageDialog(this, msj, "Entrega y Cobro", JOptionPane.INFORMATION_MESSAGE);
                refrescarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private int validarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden de la tabla primero.", "Atención", JOptionPane.WARNING_MESSAGE);
        }
        return fila;
    }

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
        for (OrdenTrabajo o : modelo.getOrdenes()) {
            dtm.addRow(new Object[]{o.getCliente(), o.getDispositivo(), o.getDiagnostico(), o.getEstado(), "$" + o.getCostoTotal()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaTaller().setVisible(true));
    }
}