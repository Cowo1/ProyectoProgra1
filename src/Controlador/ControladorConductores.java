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

public class ControladorConductores implements ActionListener {

    private ModeloConductores modelo;
    private File archivo = new File("conductores.txt");

    public ControladorConductores(ModeloConductores modelo) {
        this.modelo = modelo;
        verificarLicenciaV();
        cargarTabla();
    }

    @Override
  // Método que maneja los eventos de los botones de la vista
public void actionPerformed(ActionEvent e) {
    if (e.getSource() == modelo.getVista().btnGuardar) {
        guardar();
    } else if (e.getSource() == modelo.getVista().btnBuscar) {
        buscar();
    } else if (e.getSource() == modelo.getVista().btnEliminar) {
        eliminar();
    } else if (e.getSource() == modelo.getVista().btnLimpiar) {
        limpiar();
    } else if (e.getSource() == modelo.getVista().btnEliminarF) {
        eliminarFila();
    }
}

// Método que guarda los datos de un nuevo conductor en el archivo si pasa todas las validaciones
public void guardar() {
    String codigo = modelo.getVista().txtCodigo.getText().trim();
    String licencia = modelo.getVista().txtLicencia.getText().trim();
    String dpi = modelo.getVista().txtDPI.getText().trim();
    String nombre = modelo.getVista().txtNombre.getText().trim();
    String telefono = modelo.getVista().txtTelefono.getText().trim();
    String direccion = modelo.getVista().txtDireccion.getText().trim();
    String tipoLicencia = modelo.getVista().cbTipo.getSelectedItem().toString();
    String estado = modelo.getVista().cbEstado.getSelectedItem().toString();

    Date fechaI = modelo.getVista().dcFechaI.getDate();
    Date fechaV = modelo.getVista().dcVencimiento.getDate();

    if (fechaI == null || fechaV == null) {
        JOptionPane.showMessageDialog(null, "Debe seleccionar ambas fechas (ingreso y vencimiento).");
        return;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String fechaIngreso = sdf.format(fechaI);
    String fechaVencimiento = sdf.format(fechaV);

    if (codigo.length() != 5 || !codigo.matches("\\d+")) {
        JOptionPane.showMessageDialog(null, "El código debe contener exactamente 5 dígitos numéricos.");
        return;
    }

    if (dpi.length() != 13 || !dpi.matches("\\d+")) {
        JOptionPane.showMessageDialog(null, "El DPI debe contener exactamente 13 dígitos numéricos.");
        return;
    }

    if (licencia.length() != 13 || !licencia.matches("\\d+")) {
        JOptionPane.showMessageDialog(null, "La licencia debe contener exactamente 13 dígitos numéricos.");
        return;
    }

    if (existeCodigo(codigo)) {
        JOptionPane.showMessageDialog(null, "El código ya está registrado.");
        return;
    }

    if (existeLicencia(licencia)) {
        JOptionPane.showMessageDialog(null, "La licencia ya está registrada.");
        return;
    }

    if (existeDPI(dpi)) {
        JOptionPane.showMessageDialog(null, "El DPI ya está registrado.");
        return;
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
        String linea = String.format("%-8s | %-20s | %-12s | %-10s | %-25s | %-13s | %-15s | %-10s | %-12s | %-12s",
                codigo, nombre, dpi, telefono, direccion, licencia, tipoLicencia, estado, fechaIngreso, fechaVencimiento);
        writer.write(linea);
        writer.newLine();

        JOptionPane.showMessageDialog(null, "Conductor guardado correctamente");
        limpiar();
        verificarLicenciaV();
        cargarTabla();

    } catch (IOException ex) {
        JOptionPane.showMessageDialog(null, "Error al guardar el conductor");
    }
}

// Método que verifica si ya existe un conductor con el código proporcionado
public boolean existeCodigo(String codigo) {
    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length > 0 && datos[0].trim().equals(codigo)) {
                return true;
            }
        }
    } catch (IOException e) {
        return false;
    }
    return false;
}

// Método que verifica si ya existe un conductor con el DPI proporcionado
public boolean existeDPI(String dpi) {
    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length > 2 && datos[2].trim().equals(dpi)) {
                return true;
            }
        }
    } catch (IOException e) {
        return false;
    }
    return false;
}

// Método que verifica si ya existe un conductor con la licencia proporcionada
public boolean existeLicencia(String licencia) {
    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length > 5 && datos[5].trim().equals(licencia)) {
                return true;
            }
        }
    } catch (IOException e) {
        return false;
    }
    return false;
}

// Método que busca un conductor por su código y carga sus datos en el formulario
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

// Método que elimina un conductor por código si no está asignado a un bus
public void eliminar() {
    String codigo = modelo.getVista().txtCodigo.getText().trim();

    if (codigo.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Ingrese el código del conductor a eliminar.");
        return;
    }

    boolean encontrado = false;
    String estadoConductor = "";

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

    File temporal = new File("conductores_temp.txt");

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temporal))
    ) {
        String linea;

        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");

            if (datos.length >= 1 && datos[0].trim().equals(codigo)) {
                continue;
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

// Método que limpia los campos del formulario de la vista
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

// Método que carga todos los conductores en la tabla de la vista desde el archivo
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

// Método que elimina el conductor seleccionado en la tabla si no está asignado
public void eliminarFila() {
    int filaS = modelo.getVista().tblConductores.getSelectedRow();

    if (filaS == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
        return;
    }

    String codigoEliminar = modelo.getVista().tblConductores.getValueAt(filaS, 0).toString();
    String estado = "";
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

// Método que actualiza el estado del conductor a "Inactivo" si la licencia está vencida
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
                String fechaStr = datos[9].trim();

                if (!fechaStr.isEmpty()) {
                    try {
                        Date vencimiento = sdf.parse(fechaStr);
                        if (vencimiento.before(hoy)) {
                            datos[7] = "Inactivo";
                        }
                    } catch (ParseException ex) {
                        continue;
                    }
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

// Método que valida que un campo contenga únicamente números enteros
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
