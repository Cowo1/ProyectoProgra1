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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
       verificarRutasOcupadasYActualizarBuses();
        cargarTabla();
    }
    
    
   
   

    @Override
   // Maneja las acciones de los botones en la interfaz
public void actionPerformed(ActionEvent e) {
    if (e.getSource() == modelo.getVista().btnGuardar) {
        guardarAutobus();
        cargarTabla();
    } else if (e.getSource() == modelo.getVista().btnBuscar) {
        buscarAutobus();
    } else if (e.getSource() == modelo.getVista().btnEliminar) {
        eliminar();
    } else if (e.getSource() == modelo.getVista().btnLimpiar) {
        limpiar();
    } else if (e.getSource() == modelo.getVista().btnEliminarF) {
        eliminarFila();
    }
}

// Guarda los datos de un autobús en el archivo
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

// Busca un autobús por su placa y muestra sus datos
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

// Limpia todos los campos de entrada y búsqueda
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

// Carga los autobuses del archivo en la tabla
public void cargarTabla() {
    DefaultTableModel model = (DefaultTableModel) modelo.getVista().tblBuses.getModel();
    model.setRowCount(0);

    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length == 4) {
                for (int i = 0; i < datos.length; i++) {
                    datos[i] = datos[i].trim();
                }
                model.addRow(datos);
            }
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(null, "Error al cargar la tabla");
    }
}

// Elimina un autobús según la placa ingresada si no está asignado
public void eliminar() {
    String placaB = modelo.getVista().txtPlacaB.getText().trim();

    if (placaB.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Ingrese una placa para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(" \\| ");
            if (datos.length >= 4 && datos[0].trim().equalsIgnoreCase(placaB)) {
                String estado = datos[3].trim();
                if (estado.equalsIgnoreCase("Conductor Asignado") || estado.equalsIgnoreCase("Ruta Asignada") || estado.equalsIgnoreCase("En Ruta")) {
                    JOptionPane.showMessageDialog(null, "Este autobús está asignado. Primero desasigne antes de eliminarlo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                break;
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al verificar el estado del autobús: " + e.getMessage());
        return;
    }

    File temporal = new File("temp_autobuses.txt");
    boolean encontrado = false;

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temporal))
    ) {
        String linea;
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
        JOptionPane.showMessageDialog(null, "Error al eliminar el autobús: " + e.getMessage());
        return;
    }

    if (!encontrado) {
        JOptionPane.showMessageDialog(null, "No se encontró el autobús con esa placa.");
        return;
    }

    if (archivo.delete()) {
        if (temporal.renameTo(archivo)) {
            JOptionPane.showMessageDialog(null, "Autobús eliminado con éxito.");
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(null, "Error al renombrar el archivo temporal.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Error al eliminar el archivo original.");
    }
}

// Elimina un autobús según la fila seleccionada en la tabla
public void eliminarFila() {
    int filaS = modelo.getVista().tblBuses.getSelectedRow();

    if (filaS == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
        return;
    }

    String placaEliminar = modelo.getVista().tblBuses.getValueAt(filaS, 0).toString();

    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(" \\| ");
            if (datos.length >= 4 && datos[0].trim().equalsIgnoreCase(placaEliminar)) {
                String estado = datos[3].trim();
                if (estado.equalsIgnoreCase("Conductor Asignado") || estado.equalsIgnoreCase("Ruta Asignada") || estado.equalsIgnoreCase("En Ruta")) {
                    JOptionPane.showMessageDialog(null, "Este autobús está asignado. Primero desasígnelo antes de eliminarlo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                break;
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al verificar el estado del autobús.");
        return;
    }

    File archivoTemporal = new File("buses_temp.txt");
    boolean eliminado = false;

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        BufferedWriter writer = new BufferedWriter(new FileWriter(archivoTemporal))
    ) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");

            if (datos.length > 0 && datos[0].trim().equalsIgnoreCase(placaEliminar)) {
                eliminado = true;
                continue;
            }

            writer.write(linea);
            writer.newLine();
        }

    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al eliminar el autobús.");
        return;
    }

    if (archivo.delete() && archivoTemporal.renameTo(archivo)) {
        if (eliminado) {
            JOptionPane.showMessageDialog(null, "Autobús eliminado correctamente.");
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el autobús.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Error al reemplazar el archivo.");
    }
}

// Actualiza el estado de un bus por placa
private void actualizarEstadoBus(String placaBus, String nuevoEstado) {
    File archivo = new File("buses.txt");
    File temporal = new File("temp_buses.txt");

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temporal))
    ) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 4) {
                String placa = datos[0].trim();
                if (placa.equalsIgnoreCase(placaBus)) {
                    datos[3] = nuevoEstado;
                }

                for (int i = 0; i < datos.length; i++) {
                    datos[i] = datos[i].trim();
                }

                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar estado del bus");
    }

    archivo.delete();
    temporal.renameTo(archivo);
}

// Verifica rutas ocupadas y cambia estado de buses asignados a "En ruta"
public void verificarRutasOcupadasYActualizarBuses() {
    File archivoRutas = new File("rutas.txt");
    File archivoAsignaciones = new File("asignacion_ruta.txt");

    List<String> rutasOcupadas = new ArrayList<>();
    Map<String, String> mapaRutaPlaca = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(archivoRutas))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 6) {
                String codigoRuta = datos[0].trim();
                String estado = datos[5].trim();
                if (estado.equalsIgnoreCase("Ocupada")) {
                    rutasOcupadas.add(codigoRuta);
                }
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error leyendo rutas.txt");
        return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(archivoAsignaciones))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 6) {
                String codigoRuta = datos[0].trim();
                String placa = datos[5].trim();
                if (rutasOcupadas.contains(codigoRuta)) {
                    mapaRutaPlaca.put(codigoRuta, placa);
                }
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error leyendo asignacion_ruta.txt");
        return;
    }

    File archivoOriginal = new File("buses.txt");
    File archivoTemporal = new File("temp_buses.txt");

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivoOriginal));
        BufferedWriter writer = new BufferedWriter(new FileWriter(archivoTemporal))
    ) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 4) {
                String placa = datos[0].trim();
                String estado = datos[3].trim();

                if (mapaRutaPlaca.containsValue(placa) && estado.equalsIgnoreCase("Ruta Asignada")) {
                    datos[3] = "En ruta";
                }

                for (int i = 0; i < datos.length; i++) {
                    datos[i] = datos[i].trim();
                }

                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        }

    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error actualizando buses.txt");
        return;
    }

    archivoOriginal.delete();
    archivoTemporal.renameTo(archivoOriginal);
    cargarTabla();
}


    
   
}
