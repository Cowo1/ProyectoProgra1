/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.ModeloBuses;
import Vista.VistaBuses;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author diego
 */
public class ControladorBuses implements ActionListener {
   VistaBuses vista;
   private ModeloBuses modelo;
   File archivo = new File("buses.txt");

    public ControladorBuses(ModeloBuses modelo) {
        this.modelo = modelo;
        cargarTabla();
    }
    
    
   
   

    @Override
    public void actionPerformed(ActionEvent e) {
   if (e.getSource() == modelo.getVista().btnGuardar) {
            guardarAutobus();
        } else if (e.getSource() == modelo.getVista().btnBuscar) {
            buscarAutobus();
        } else if (e.getSource() == modelo.getVista().btnEliminar) {
            eliminar();
        } else if (e.getSource() == modelo.getVista().btnLimpiar) {
            limpiar();
        }else if (e.getSource() == modelo.getVista().btnEliminarF) {
           eliminarFila();
        }
    }
    
    
 public void guardarAutobus() {
        String placa = modelo.getVista().txtPlaca.getText();
        String modeloBus = modelo.getVista().txtModelo.getText();
        int capacidad = (Integer) modelo.getVista().spCapacidad.getValue();
        String estado = (String) modelo.getVista().cbEstado.getSelectedItem();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
            writer.write(placa + " | " + modeloBus + " | " + capacidad + " | " + estado);
            writer.newLine();
            JOptionPane.showMessageDialog(null, "Autobus guardado correctamente");
            limpiar();
            cargarTabla();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el autobus");
        }
    }
     public void buscarAutobus() {
        String placaB = modelo.getVista().txtPlacaB.getText();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean encontrado = false;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\| ");
                if (datos.length >= 4 && datos[0].trim().equalsIgnoreCase(placaB)) {
                    modelo.getVista().txtModeloB.setText(datos[1].trim());
                    modelo.getVista().txtCapacidadB.setText(datos[2].trim());
                    modelo.getVista().txtEstadoB.setText(datos[3].trim());
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "Autobus no encontrado");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar el autobus");
        }
    }
     
     

    public void limpiar() {
        modelo.getVista().txtPlaca.setText("");
        modelo.getVista().txtModelo.setText("");
        modelo.getVista().spCapacidad.setValue(0);
        modelo.getVista().cbEstado.setSelectedIndex(0);

        modelo.getVista().txtPlacaB.setText("");
        modelo.getVista().txtModeloB.setText("");
        modelo.getVista().txtCapacidadB.setText("");
        modelo.getVista().txtEstadoB.setText("");
    }
    
    
    
    public void cargarTabla() {
        DefaultTableModel model = (DefaultTableModel) modelo.getVista().tblBuses.getModel();
        model.setRowCount(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if(datos.length == 4){
                    for(int i = 0; i < datos.length; i++){
                        datos[i] = datos[i].trim();
                    }
                    model.addRow(datos);
                }
               
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar la tabla");
        }
    }
    
    
   public void eliminar() {
        String placaB = modelo.getVista().txtPlacaB.getText().trim();
        if (placaB.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese una placa para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File temporal = new File("temp_autobuses.txt");

        try (
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            BufferedWriter writer = new BufferedWriter(new FileWriter(temporal));
        ) {
            String linea;
            boolean encontrado = false;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(" \\| ");
                if (datos.length > 0 && datos[0].equalsIgnoreCase(placaB)) {
                    encontrado = true;
                    continue;
                }
                writer.write(linea);
                writer.newLine();
            }
            } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el autobus: " + e.getMessage());
            return;
            }

           if (archivo.delete()) {
        if (temporal.renameTo(archivo)) {
            JOptionPane.showMessageDialog(null, "Autobus eliminado con exito.");
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(null, "Error al renombrar el archivo temporal.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Error al eliminar el archivo original.");
    }

        
    }
   public void eliminarFila() {
    int filaS = modelo.getVista().tblBuses.getSelectedRow();

    if (filaS == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
        return;
    }

    String placaEliminar = modelo.getVista().tblBuses.getValueAt(filaS, 0).toString();

    File archivoTemporal = new File("buses_temp.txt");
    boolean eliminado = false;

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        BufferedWriter writer = new BufferedWriter(new FileWriter(archivoTemporal));
    ) {
        String linea;

        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");

            if (datos.length > 0 && datos[0].trim().equals(placaEliminar)) {
                eliminado = true;
                continue; 
            }

            writer.write(linea);
            writer.newLine();
        }
        

    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al eliminar el autobus.");
        return;
    }

    if (archivo.delete() && archivoTemporal.renameTo(archivo)) {
        if (eliminado) {
            JOptionPane.showMessageDialog(null, "Autobus eliminado correctamente.");
            cargarTabla(); 
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el bus.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Error al reemplazar el archivo.");
    }
}
    
   
}
