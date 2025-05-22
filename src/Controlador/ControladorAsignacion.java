/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.ModeloAsignacion;
import Vista.VistaAsignarConductor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author diego
 */
public class ControladorAsignacion implements ActionListener {

    private ModeloAsignacion modelo;

    private VistaAsignarConductor vista;
    private File archivoConductores = new File("conductores.txt");
    private File archivoBuses = new File("buses.txt");
    private File archivoAsignaciones = new File("asignaciones.txt");

    public ControladorAsignacion(ModeloAsignacion modelo) {
        modelo.getVista().tblConductoresA.getSelectionModel().addListSelectionListener(e -> {
    int fila = modelo.getVista().tblConductoresA.getSelectedRow();
    if (fila != -1) {
        String codigo = modelo.getVista().tblConductoresA.getValueAt(fila, 0).toString();
        modelo.getVista().txtCodigo.setText(codigo);
    }
});
        modelo.getVista().cmbBusesA.addItemListener(new ItemListener() {
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String placaS = e.getItem().toString();
            mostrarDatosB(placaS);
        }
    }
});
         this.modelo = modelo;
        this.vista = modelo.getVista();
         cargarBuses();
         cargarConductores();
       
       
       
        vista.btnAsignar.addActionListener(this);
        vista.btnDesasignar.addActionListener(this);

    }

    public void cargarConductores() {
        DefaultTableModel model = (DefaultTableModel) vista.tblConductoresA.getModel();
        model.setRowCount(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoConductores))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");

                if (datos.length >= 10) {
                    String estado = datos[7].trim();
                    String fechaV = datos[9].trim();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date FechaL = sdf.parse(fechaV);

                    if (estado.equalsIgnoreCase("Disponible")|| estado.equalsIgnoreCase("En ruta") && FechaL.after(new Date())) {
                        model.addRow(new Object[]{
                            datos[0].trim(),
                            datos[1].trim(),
                            datos[2].trim(),
                            datos[5].trim(),
                            datos[7].trim(),
                            datos[8].trim()
                        });
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar conductores disponibles.");
        }
    }

    public void cargarBuses() {
        vista.cmbBusesA.removeAllItems();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoBuses))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 4 && datos[3].trim().equalsIgnoreCase("Disponible")|| datos[3].trim().equalsIgnoreCase("En ruta")) {
                    vista.cmbBusesA.addItem(datos[0].trim());
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar buses disponibles.");
        }
    }

    public void asignarConductor() {
        int fila = vista.tblConductoresA.getSelectedRow();
        String placa = (String) vista.cmbBusesA.getSelectedItem();

        if (fila == -1 || placa == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un conductor y un autobús.");
            return;
        }

        String codigo = vista.tblConductoresA.getValueAt(fila, 0).toString();
        String nombre = vista.tblConductoresA.getValueAt(fila, 1).toString();
        String fechaAsignacion = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoAsignaciones, true))) {
            writer.write(codigo + " | " + nombre + " | " + placa + " | " + fechaAsignacion);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar la asignación.");
            return;
        }

        actualizarConductor(codigo, "En ruta");
        actualizarBus(placa, "En ruta");

        JOptionPane.showMessageDialog(null, "Conductor asignado correctamente.");
        cargarConductores();
        cargarBuses();
    }

    public void desasignarConductor() {
        String codigo = vista.txtCodigo.getText().trim();
        String placa = (String) vista.cmbBusesA.getSelectedItem();

        if (codigo.isEmpty() || placa == null) {
            JOptionPane.showMessageDialog(null, "Ingrese DPI del conductor y seleccione un autobús.");
            return;
        }

        actualizarConductor(codigo, "Disponible");
        actualizarBus(placa, "Disponible");
        eliminarAsignacion(codigo);

        JOptionPane.showMessageDialog(null, "Conductor desasignado correctamente.");
        cargarConductores();
        cargarBuses();
    }

    public void actualizarConductor(String codigo, String nuevoE) {
        File temporal = new File("conductoresTemp.txt");

        try (
                BufferedReader reader = new BufferedReader(new FileReader(archivoConductores)); BufferedWriter writer = new BufferedWriter(new FileWriter(temporal))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 10 && datos[0].trim().equals(codigo)) {
                    datos[7] = nuevoE;
                }
                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar estado del conductor.");
        }

        archivoConductores.delete();
        temporal.renameTo(archivoConductores);
    }

    public void actualizarBus(String placa, String nuevoE) {
        File temporal = new File("busesTemp.txt");

        try (
                BufferedReader reader = new BufferedReader(new FileReader(archivoBuses)); BufferedWriter writer = new BufferedWriter(new FileWriter(temporal))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 4 && datos[0].trim().equals(placa)) {
                    datos[3] = nuevoE; 
                }
                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar estado del autobús.");
        }

        archivoBuses.delete();
        temporal.renameTo(archivoBuses);
    }

    public void eliminarAsignacion(String codigo) {
        File temporal = new File("asignacionesTemp.txt");

        try (
                BufferedReader reader = new BufferedReader(new FileReader(archivoAsignaciones)); BufferedWriter writer = new BufferedWriter(new FileWriter(temporal))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (!datos[0].trim().equals(codigo)) {
                    writer.write(linea);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar asignacion.");
        }

        archivoAsignaciones.delete();
        temporal.renameTo(archivoAsignaciones);
    }
    public void mostrarDatosB(String placa) {
    try (BufferedReader reader = new BufferedReader(new FileReader("buses.txt"))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 4 && datos[0].trim().equalsIgnoreCase(placa)) {
              
                modelo.getVista().txtModelo.setText(datos[1].trim());
                modelo.getVista().txtCapacidad.setText(datos[2].trim());
                return;
            }
        }

        // Si no encuentra el bus, limpia los campos
        modelo.getVista().txtModelo.setText("");
        modelo.getVista().txtCapacidad.setText("");

    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al buscar los datos del autobús.");
    }
}


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modelo.getVista().btnAsignar) {
            asignarConductor();
        } else if (e.getSource() == vista.btnDesasignar) {
            desasignarConductor();
        }

    }
}
