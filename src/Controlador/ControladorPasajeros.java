/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author diego
 */
import Modelo.ModeloPasajeros;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


public class ControladorPasajeros implements ActionListener {

    private ModeloPasajeros modelo;
    private File archivoPasajeros = new File("pasajeros.txt");

    public ControladorPasajeros(ModeloPasajeros modelo) {
        this.modelo = modelo;

        // Eventos
        

        // Selección en la tabla
        modelo.getVista().tblPasajeros.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                llenarCamposDesdeTabla();
            }
        });

        cargarTabla();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modelo.getVista().btnBuscar) {
            buscarPasajero();
        } else if (e.getSource() == modelo.getVista().btnEliminar) {
            eliminarPasajero();
        } else if (e.getSource() == modelo.getVista().btnLimpiar) {
            limpiarCampos();
        }if (e.getSource() == modelo.getVista().btnEliminarF) {
    eliminarFilaSeleccionada();
}

    }

    public void cargarTabla() {
        DefaultTableModel modeloTabla = (DefaultTableModel) modelo.getVista().tblPasajeros.getModel();
        modeloTabla.setRowCount(0); // limpiar tabla

        try (BufferedReader br = new BufferedReader(new FileReader(archivoPasajeros))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length == 4) {
                    Object[] fila = new Object[4];
                    for (int i = 0; i < 4; i++) {
                        fila[i] = datos[i].trim();
                    }
                    modeloTabla.addRow(fila);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void llenarCamposDesdeTabla() {
        int fila = modelo.getVista().tblPasajeros.getSelectedRow();
        if (fila != -1) {
            JTable tabla = modelo.getVista().tblPasajeros;
            modelo.getVista().txtNombre.setText(tabla.getValueAt(fila, 0).toString());
            modelo.getVista().txtDpi.setText(tabla.getValueAt(fila, 1).toString());
            modelo.getVista().txtTelefono.setText(tabla.getValueAt(fila, 2).toString());         
            modelo.getVista().txtFechaDeViaje.setText(tabla.getValueAt(fila, 3).toString());
        }
    }

    private void buscarPasajero() {
        String dpi = modelo.getVista().txtDpi.getText().trim();
        if (dpi.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un DPI para buscar.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivoPasajeros))) {
            String linea;
            boolean encontrado = false;

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length == 4 && datos[0].trim().equals(dpi)) {
                    modelo.getVista().txtTelefono.setText(datos[1].trim());
                    modelo.getVista().txtNombre.setText(datos[2].trim());
                    
                    modelo.getVista().txtFechaDeViaje.setText(datos[3].trim());
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "Pasajero no encontrado.");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void eliminarPasajero() {
        String dpi = modelo.getVista().txtDpi.getText().trim();
        if (dpi.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un DPI para eliminar.");
            return;
        }

        File temp = new File("temporal.txt");

        try (
            BufferedReader br = new BufferedReader(new FileReader(archivoPasajeros));
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp))
        ) {
            String linea;
            boolean eliminado = false;

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (!datos[0].trim().equals(dpi)) {
                    bw.write(linea);
                    bw.newLine();
                } else {
                    eliminado = true;
                }
            }

            if (eliminado) {
                archivoPasajeros.delete();
                temp.renameTo(archivoPasajeros);
                JOptionPane.showMessageDialog(null, "Pasajero eliminado correctamente.");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el pasajero para eliminar.");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarCampos() {
        modelo.getVista().txtDpi.setText("");
        modelo.getVista().txtTelefono.setText("");
        modelo.getVista().txtNombre.setText("");
        
        modelo.getVista().txtFechaDeViaje.setText("");
        modelo.getVista().tblPasajeros.clearSelection();
    }
    public void eliminarFilaSeleccionada() {
    int fila = modelo.getVista().tblPasajeros.getSelectedRow();

    if (fila == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
        return;
    }

    // Obtener el DPI desde la fila seleccionada
    String dpiSeleccionado = modelo.getVista().tblPasajeros.getValueAt(fila, 1).toString();

    File temporal = new File("temporal.txt");

    try (
        BufferedReader br = new BufferedReader(new FileReader(archivoPasajeros));
        BufferedWriter bw = new BufferedWriter(new FileWriter(temporal))
    ) {
        String linea;
        boolean eliminado = false;

        while ((linea = br.readLine()) != null) {
            String[] datos = linea.split("\\|");

            // Saltar líneas mal formateadas
            if (datos.length != 4) {
                bw.write(linea);
                bw.newLine();
                continue;
            }

            String dpi = datos[1].trim();

            if (!dpi.equals(dpiSeleccionado)) {
                bw.write(linea);
                bw.newLine();
            } else {
                eliminado = true;
            }
        }

        br.close();
        bw.close();

        if (archivoPasajeros.delete() && temporal.renameTo(archivoPasajeros)) {
            if (eliminado) {
                JOptionPane.showMessageDialog(null, "Pasajero eliminado correctamente.");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el pasajero a eliminar.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al actualizar el archivo.");
        }

    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al eliminar el pasajero.");
    }
}

}

