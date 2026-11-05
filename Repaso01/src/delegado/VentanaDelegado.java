package delegado;

import modelo.Componente;
import modelo.GestorTiendaModelo;
import util.UtilGenerico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VentanaDelegado extends JFrame {
    private GestorTiendaModelo modelo; // Referencia al Modelo
    
    // Componentes de la Vista
    private JTable tabla;
    private DefaultTableModel dtm;
    private JTextField txtNombre, txtPrecio, txtStock;
    private JComboBox<String> cbCategoria;
    
    // Paleta de colores 
    private Color bgOscuro = new Color(10, 25, 47);
    private Color fgBlanco = Color.WHITE;

    public VentanaDelegado() {
        modelo = new GestorTiendaModelo(); 
        configurarVentana();
        inicializarUI();
        refrescarTabla();
    }

    private void configurarVentana() {
        setTitle("TechStore v2.0 - Gestión de Ventas");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgOscuro);
        setLayout(new BorderLayout());
    }

    private void inicializarUI() {
        // --- PANEL NORTE (Vista: Formulario) ---
        JPanel panelNorte = new JPanel(new GridLayout(2, 5, 5, 5));
        panelNorte.setBackground(bgOscuro);
        panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtNombre = crearTextField();
        txtPrecio = crearTextField();
        txtStock = crearTextField();
        
        // Uso de método genérico varargs con tipo String para llenar el ComboBox
        String[] catArr = UtilGenerico.crearLista("Procesador", "Gráfica", "RAM", "Almacenamiento").toArray(new String[0]);
        cbCategoria = new JComboBox<>(catArr);
        
        JButton btnAgregar = crearBoton("Agregar Componente");

        panelNorte.add(crearLabel("Nombre:"));
        panelNorte.add(crearLabel("Categoría:"));
        panelNorte.add(crearLabel("Precio ($):"));
        panelNorte.add(crearLabel("Stock:"));
        panelNorte.add(new JLabel("")); // Espaciador visual
        
        panelNorte.add(txtNombre);
        panelNorte.add(cbCategoria);
        panelNorte.add(txtPrecio);
        panelNorte.add(txtStock);
        panelNorte.add(btnAgregar);
        
        add(panelNorte, BorderLayout.NORTH);

        // --- PANEL CENTRAL (Vista: Tabla) ---
        dtm = new DefaultTableModel(new String[]{"Nombre", "Categoría", "Precio", "Stock"}, 0);
        tabla = new JTable(dtm);
        tabla.setBackground(bgOscuro);
        tabla.setForeground(fgBlanco);
        tabla.setGridColor(Color.GRAY);
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(bgOscuro);
        add(scroll, BorderLayout.CENTER);

        // --- PANEL SUR (Vista: Botón de Venta) ---
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.setBackground(bgOscuro);
        JButton btnVender = crearBoton("Procesar Venta");
        panelSur.add(btnVender);
        add(panelSur, BorderLayout.SOUTH);

        // --- CONTROLADOR (Captura de Eventos y Delegación) ---
        btnAgregar.addActionListener((ActionEvent e) -> {
            try {
                Componente c = new Componente(
                    txtNombre.getText(),
                    cbCategoria.getSelectedItem().toString(),
                    Double.parseDouble(txtPrecio.getText()),
                    Integer.parseInt(txtStock.getText())
                );
                modelo.agregarComponente(c); // Delega la inserción al modelo
                refrescarTabla();
                txtNombre.setText(""); txtPrecio.setText(""); txtStock.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Asegúrese de ingresar números válidos en Precio y Stock.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVender.addActionListener((ActionEvent e) -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un componente de la tabla.");
                return;
            }
            String input = JOptionPane.showInputDialog(this, "Cantidad a vender:");
            if (input == null || input.trim().isEmpty()) return;

            try {
                int cant = Integer.parseInt(input);
                double[] totales = modelo.procesarVenta(fila, cant); // Delega el cálculo de negocio al modelo
                
                String msj = String.format("¡Venta procesada!\nSubtotal: $%.2f\nIVA (15%%): $%.2f\nTotal a Pagar: $%.2f", 
                                           totales[0], totales[1], totales[2]);
                JOptionPane.showMessageDialog(this, msj, "Comprobante", JOptionPane.INFORMATION_MESSAGE);
                refrescarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Venta", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Métodos auxiliares para la creación de componentes visuales (Vista pura)
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(fgBlanco);
        return lbl;
    }

    private JTextField crearTextField() {
        JTextField txt = new JTextField();
        txt.setBackground(fgBlanco);
        txt.setForeground(Color.BLACK);
        return txt;
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(fgBlanco);
        btn.setForeground(bgOscuro);
        return btn;
    }

    private void refrescarTabla() {
        dtm.setRowCount(0);
        for (Componente c : modelo.getInventario()) { // Consulta al modelo
            dtm.addRow(new Object[]{c.getNombre(), c.getCategoria(), c.getPrecio(), c.getStock()});
        }
    }

    // Punto de entrada principal
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaDelegado().setVisible(true));
    }
}