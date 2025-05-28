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
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author diego
 */
public class ControladorAsignacion implements ActionListener {

    private ModeloAsignacion modelo;

    private File archivoConductores = new File("conductores.txt");
    private File archivoBuses = new File("buses.txt");
    private File archivoAsignaciones = new File("asignaciones.txt");

    public ControladorAsignacion(ModeloAsignacion modelo) {
        this.modelo = modelo;

       modelo.getVista().tblConductoresA.getSelectionModel().addListSelectionListener(e -> {
    int fila = modelo.getVista().tblConductoresA.getSelectedRow();
    if (fila != -1) {
        String codigo = modelo.getVista().tblConductoresA.getValueAt(fila, 0).toString();
        modelo.getVista().txtCodigo.setText(codigo);

        // Buscar en archivo de asignaciones
        String placaAsignada = obtenerBusAsignado(codigo);
        if (placaAsignada != null) {
            modelo.getVista().txtPlacaA.setText(placaAsignada);
        } else {
            modelo.getVista().txtPlacaA.setText("Sin asignación");
        }
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

        cargarBuses();
        cargarConductores();

        modelo.getVista().btnAsignar.addActionListener(this);
        modelo.getVista().btnDesasignar.addActionListener(this);
    }
                
    public void cargarConductores() {
        DefaultTableModel model = (DefaultTableModel) modelo.getVista().tblConductoresA.getModel();
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

                    if (estado.equalsIgnoreCase("Disponible") || estado.equalsIgnoreCase("Asignado") && FechaL.after(new Date())) {
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
        modelo.getVista().cmbBusesA.removeAllItems();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoBuses))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 4 && datos[3].trim().equalsIgnoreCase("Disponible") || datos[3].trim().equalsIgnoreCase("En ruta")) {
                    modelo.getVista().cmbBusesA.addItem(datos[0].trim());
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar buses disponibles.");
        }
    }

    public void asignarConductor() {
        int fila = modelo.getVista().tblConductoresA.getSelectedRow();
        String placa = (String) modelo.getVista().cmbBusesA.getSelectedItem();

        if (fila == -1 || placa == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un conductor y un autobús.");
            return;
        }

        String codigo = modelo.getVista().tblConductoresA.getValueAt(fila, 0).toString();
        String nombre = modelo.getVista().tblConductoresA.getValueAt(fila, 1).toString();
        String fechaAsignacion = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoAsignaciones, true))) {
            writer.write(codigo + " | " + nombre + " | " + placa + " | " + fechaAsignacion);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar la asignación.");
            return;
        }

        actualizarConductor(codigo, "Asignado");
        actualizarBus(placa, "Asignado");

        JOptionPane.showMessageDialog(null, "Conductor asignado correctamente.");
        cargarConductores();
        cargarBuses();
        limpiar();
    }

    public void desasignarConductor() {
    String codigo = modelo.getVista().txtCodigo.getText().trim();
    String placa = modelo.getVista().txtPlacaA.getText().trim();

    if (codigo.isEmpty() || placa.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Ingrese el código del conductor y seleccione un autobús.");
        return;
    }

    if (placa.equalsIgnoreCase("Sin asignación") || placa.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Este conductor no tiene un bus asignado.");
        return;
    }

    // Paso 1: mover línea de asignaciones.txt a desasignaciones.txt
    File archivoTemporal = new File("asignacionesTemp.txt");
    File archivoAsignaciones = new File("asignaciones.txt");
    File archivoDesasignaciones = new File("desasignaciones.txt");

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivoAsignaciones));
        BufferedWriter writer = new BufferedWriter(new FileWriter(archivoTemporal));
        BufferedWriter writerDesasignaciones = new BufferedWriter(new FileWriter(archivoDesasignaciones, true));
    ) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 3 && datos[0].trim().equals(codigo)) {
                // Es la asignación a eliminar, la guardamos en desasignaciones.txt
                writerDesasignaciones.write(linea + " | " + new SimpleDateFormat("yyyy-MM-dd").format(new Date())); // agregamos fecha de desasignación
                writerDesasignaciones.newLine();
                continue; // no escribir en el nuevo archivo
            }
            writer.write(linea);
            writer.newLine();
        }

    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al procesar desasignación: " + e.getMessage());
        return;
    }

    // Reemplazar archivo original
    if (archivoAsignaciones.delete() && archivoTemporal.renameTo(archivoAsignaciones)) {
        actualizarConductor(codigo, "Disponible");
        actualizarBus(placa, "Disponible");

        JOptionPane.showMessageDialog(null, "Conductor desasignado correctamente.");
        cargarConductores();
        cargarBuses();
        limpiar();
    } else {
        JOptionPane.showMessageDialog(null, "Error al actualizar asignaciones.");
    }
}


    public void actualizarConductor(String codigo, String nuevoE) {
        File temporal = new File("conductoresTemp.txt");

        try (
                BufferedReader reader = new BufferedReader(new FileReader(archivoConductores));
                BufferedWriter writer = new BufferedWriter(new FileWriter(temporal))) {
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
                BufferedReader reader = new BufferedReader(new FileReader(archivoBuses));
                BufferedWriter writer = new BufferedWriter(new FileWriter(temporal))) {
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
                BufferedReader reader = new BufferedReader(new FileReader(archivoAsignaciones));
                BufferedWriter writer = new BufferedWriter(new FileWriter(temporal))) {
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
        } else if (e.getSource() == modelo.getVista().btnDesasignar) {
            desasignarConductor();
        }
    }
    public void mostrarBusAsignado(String codigoConductor) {
    String placaBus = "Sin asignación";

    try (BufferedReader reader = new BufferedReader(new FileReader(archivoAsignaciones))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 3 && datos[0].trim().equals(codigoConductor)) {
                placaBus = datos[2].trim(); // La placa del bus asignado
                break;
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al buscar asignación del conductor.");
    }

  
}
    public void limpiar() {
    // Limpiar campo de código
    modelo.getVista().txtCodigo.setText("");

    // Limpiar campo de autobús asignado (JTextField)
    modelo.getVista().txtPlacaA.setText("");

    // Deseleccionar item en el combo de buses disponibles, sin agregar espacios en blanco
    if (modelo.getVista().cmbBusesA.getItemCount() > 0) {
        modelo.getVista().cmbBusesA.setSelectedIndex(-1);
    }
}
public String obtenerBusAsignado(String codigo) {
    try (BufferedReader reader = new BufferedReader(new FileReader(archivoAsignaciones))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 3 && datos[0].trim().equals(codigo)) {
                return datos[2].trim(); // Placa del bus asignado
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al buscar bus asignado.");
    }
    return null; // No tiene asignación
}


}

