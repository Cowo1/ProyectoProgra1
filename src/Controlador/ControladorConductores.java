/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.ModeloConductores;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

public class ControladorConductores implements ActionListener {

    private ModeloConductores modelo;
    private File archivo = new File("conductores.txt");

    public ControladorConductores(ModeloConductores modelo) {
        this.modelo = modelo;
       
        modelo.getVista().btnGuardar.addActionListener(this);
        modelo.getVista().btnBuscar.addActionListener(this);
        modelo.getVista().btnEliminar.addActionListener(this);
        modelo.getVista().btnLimpiar.addActionListener(this);
        verificarLicenciaV();
        cargarTabla();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == modelo.getVista().btnGuardar){
            guardar();
            verificarLicenciaV();
            cargarTabla();
            
        }else if(e.getSource() == modelo.getVista().btnBuscar){
            buscar();  
        }else if(e.getSource() == modelo.getVista().btnEliminar){
        eliminar();
        }else if(e.getSource() == modelo.getVista().btnLimpiar){
            limpiar();
        }else if(e.getSource() == modelo.getVista().btnEliminarF){
        eliminarFila();
        }
    }

    public void guardar() {
   
        String codigo = modelo.getVista().txtCodigo.getText().trim();
       String licencia = modelo.getVista().txtLicencia.getText().trim();
        String dpi = modelo.getVista().txtDPI.getText().trim();
        
         if (!entero(codigo, "Codigo") || !entero(dpi, "DPI") || !entero(licencia, "Licencia")) {
        return; 
    }
          String nombre = modelo.getVista().txtNombre.getText().trim();
        String telefono = modelo.getVista().txtTelefono.getText().trim();
        String direccion = modelo.getVista().txtDireccion.getText().trim();
        String tipoLicencia = modelo.getVista().cbTipo.getSelectedItem().toString();
        String estado = modelo.getVista().cbEstado.getSelectedItem().toString();
        
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        String fechaIngreso = sdf.format(modelo.getVista().dcFechaI.getDate());
        String fechaVencimiento = sdf.format(modelo.getVista().dcVencimiento.getDate());
 if (fechaIngreso == null || fechaVencimiento == null) {
    JOptionPane.showMessageDialog(null, "Debe seleccionar ambas fechas (ingreso y vencimiento).");
    return;
}
         try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
       
        String linea = String.format("%-8s | %-20s | %-12s | %-10s | %-25s | %-10s | %-15s | %-10s | %-12s | %-12s",
            codigo, nombre, dpi, telefono, direccion, licencia, tipoLicencia, estado, fechaIngreso, fechaVencimiento);
        
        writer.write(linea);
        writer.newLine();
            
            JOptionPane.showMessageDialog(null,"Conductor guardado correctamente");
            limpiar();
          
            
        } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,"Error al guardar el conductor");

        }
    }
    
    public void buscar(){
        String codigo = modelo.getVista().txtCodigo.getText().trim();
         if (!entero(codigo, "Codigo")) {
        return; 
    }
        if(codigo.isEmpty()){
         JOptionPane.showMessageDialog(null,"Ingrese el codigo del conductor a buscar");
         return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean encontrado = false;

            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 10 && datos[0].trim().equals(codigo)) {
                    modelo.getVista().txtNombre.setText(datos[1].trim());
                    modelo.getVista().txtDPI.setText(datos[2].trim());
                    modelo.getVista().txtTelefono.setText(datos[3].trim());
                    modelo.getVista().txtDireccion.setText(datos[4].trim());
                    modelo.getVista().txtLicencia.setText(datos[5].trim());
                    modelo.getVista().cbTipo.setSelectedItem(datos[6].trim());
                    modelo.getVista().cbEstado.setSelectedItem(datos[7].trim());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    modelo.getVista().dcFechaI.setDate(sdf.parse(datos[8].trim()));
                    modelo.getVista().dcVencimiento.setDate(sdf.parse(datos[9].trim()));
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "Conductor no encontrado.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar el conductor.");
        }
    }
  public void eliminar() {
    String codigo = modelo.getVista().txtCodigo.getText().trim();

    if (codigo.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Ingrese el código del conductor a eliminar.");
        return;
    }

    boolean encontrado = false;
    String estadoConductor = "";

    // Verificar estado del conductor
    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;

        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");

            if (datos.length >= 8 && datos[0].trim().equals(codigo)) {
                estadoConductor = datos[7].trim();
                encontrado = true;
                break;
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al verificar el estado del conductor.");
        return;
    }

    if (!encontrado) {
        JOptionPane.showMessageDialog(null, "No se encontró un conductor con ese código.");
        return;
    }

    if (estadoConductor.equalsIgnoreCase("Asignado")) {
        JOptionPane.showMessageDialog(null, "Este conductor tiene un bus asignado. Debe desasignarlo antes de poder eliminarlo.");
        return;
    }

    // Eliminar conductor si NO está asignado
    File temporal = new File("conductores_temp.txt");

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temporal))
    ) {
        String linea;

        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");

            if (datos.length >= 1 && datos[0].trim().equals(codigo)) {
                continue; // No se escribe esta línea
            }

            writer.write(linea);
            writer.newLine();
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al eliminar el conductor.");
        return;
    }

    if (archivo.delete() && temporal.renameTo(archivo)) {
        JOptionPane.showMessageDialog(null, "Conductor eliminado correctamente.");
        limpiar();
        cargarTabla();
    } else {
        JOptionPane.showMessageDialog(null, "Error al reemplazar el archivo.");
    }
}

    

    public void limpiar() {
        modelo.getVista().txtCodigo.setText("");
        modelo.getVista().txtNombre.setText("");
        modelo.getVista().txtDPI.setText("");
        modelo.getVista().txtTelefono.setText("");
        modelo.getVista().txtDireccion.setText("");
        modelo.getVista().txtLicencia.setText("");
        modelo.getVista().cbTipo.setSelectedIndex(0);
        modelo.getVista().cbEstado.setSelectedIndex(0);
        modelo.getVista().dcFechaI.setDate(null);
        modelo.getVista().dcVencimiento.setDate(null);
    }
    
    public void cargarTabla(){
         DefaultTableModel model = (DefaultTableModel) modelo.getVista().tblConductores.getModel();
        model.setRowCount(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 10) {
                   
                    model.addRow(new Object[]{
                        datos[0].trim(), 
                        datos[1].trim(), 
                        datos[3].trim(),
                        datos[5].trim(),
                        datos[7].trim(), 
                        datos[8].trim()
                    });
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar la tabla de conductores.");
        }
    }
  public void eliminarFila() {
    int filaS = modelo.getVista().tblConductores.getSelectedRow();

    if (filaS == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
        return;
    }

    String codigoEliminar = modelo.getVista().tblConductores.getValueAt(filaS, 0).toString();
    String estado = "";

    // Verificar el estado del conductor
    boolean encontrado = false;
    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 8 && datos[0].trim().equals(codigoEliminar)) {
                estado = datos[7].trim();
                encontrado = true;
                break;
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al verificar el estado del conductor.");
        return;
    }

    if (!encontrado) {
        JOptionPane.showMessageDialog(null, "No se encontró el conductor en el archivo.");
        return;
    }

    if (estado.equalsIgnoreCase("Asignado")) {
        JOptionPane.showMessageDialog(null, "Este conductor tiene un bus asignado. Debe desasignarlo antes de poder eliminarlo.");
        return;
    }

    // Proceder con la eliminación si no está asignado
    File archivoTemporal = new File("conductores_temp.txt");
    boolean eliminado = false;

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        BufferedWriter writer = new BufferedWriter(new FileWriter(archivoTemporal))
    ) {
        String linea;

        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");

            if (datos.length > 0 && datos[0].trim().equals(codigoEliminar)) {
                eliminado = true;
                continue; 
            }

            writer.write(linea);
            writer.newLine();
        }

    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al eliminar el conductor.");
        return;
    }

    if (archivo.delete() && archivoTemporal.renameTo(archivo)) {
        if (eliminado) {
            JOptionPane.showMessageDialog(null, "Conductor eliminado correctamente.");
            cargarTabla(); 
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el conductor.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Error al reemplazar el archivo.");
    }
}

    public void verificarLicenciaV() {
    File temporal = new File("conductores_temp.txt");
    Date hoy = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temporal));
    ) {
        String linea;

        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length == 10) {
                String estado = datos[7].trim();
                Date vencimiento = sdf.parse(datos[9].trim());

                if (vencimiento.before(hoy)) {
                    datos[7] = "Inactivo"; 
                }

                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        }

        reader.close();
        writer.close();

        archivo.delete();
        temporal.renameTo(archivo);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al verificar licencias vencidas.");
    }
}
    public boolean entero(String texto, String campo) {
    try {
        Integer.parseInt(texto);
        return true;
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "El campo " + campo + " debe contener solo números enteros.", "Entrada inválida", JOptionPane.WARNING_MESSAGE);
        return false;
    }
}
    
    
}
