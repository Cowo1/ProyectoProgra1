/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;


import Modelo.ModeloAsignarBus;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControladorAsignarBus implements ActionListener {

    private ModeloAsignarBus modelo;
    private File archivoRutas = new File("rutas.txt");
    private File archivoBuses = new File("buses.txt");
    private File archivoAsignaciones = new File("asignaciones.txt");
    private File archivoAsignRuta = new File("asignacion_ruta.txt");

    public ControladorAsignarBus(ModeloAsignarBus modelo) {
        this.modelo = modelo;

        cargarRutas();
        cargarBuses();

        modelo.getVista().tblRutasA.getSelectionModel().addListSelectionListener(e -> {
            int fila = modelo.getVista().tblRutasA.getSelectedRow();
            if (fila != -1) {
                String codigo = modelo.getVista().tblRutasA.getValueAt(fila, 0).toString();
                modelo.getVista().txtCodigoR.setText(codigo);
                mostrarDatosRuta(codigo);
            }
        });

        modelo.getVista().cmbBusesA.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                mostrarDatosBus(e.getItem().toString());
            }
        });
        modelo.getVista().btnDesasignar.addActionListener(e -> desasignarBusDeRuta());

        modelo.getVista().btnAsignar.addActionListener(this);
    }

    public void cargarRutas() {
        DefaultTableModel model = (DefaultTableModel) modelo.getVista().tblRutasA.getModel();
        model.setRowCount(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoRutas))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 6) {
                    String estado = datos[5].trim();
                    if (estado.equalsIgnoreCase("Activa") || estado.equalsIgnoreCase("Asignada")) {
                        model.addRow(new Object[]{
                                datos[0].trim(), datos[1].trim(), datos[2].trim(), datos[3].trim(), datos[4].trim(), datos[5].trim()
                        });
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar rutas");
        }
    }

    public void cargarBuses() {
        modelo.getVista().cmbBusesA.removeAllItems();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoBuses))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 4 && datos[3].trim().equalsIgnoreCase("Asignado")) {
                    modelo.getVista().cmbBusesA.addItem(datos[0].trim());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar buses disponibles");
        }
    }

    public void mostrarDatosRuta(String codigo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoRutas))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos[0].trim().equals(codigo)) {
                    modelo.getVista().txtOrigenR.setText(datos[1].trim());
                    modelo.getVista().txtDestinoR.setText(datos[2].trim());
                    modelo.getVista().txtHorarioR.setText(datos[3].trim());
                    modelo.getVista().txtPrecioR.setText(datos[4].trim());
                    break;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar datos de la ruta");
        }
    }

    public void mostrarDatosBus(String placa) {
        try (BufferedReader reader = new BufferedReader(new FileReader("buses.txt"))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 4 && datos[0].trim().equalsIgnoreCase(placa)) {
                    modelo.getVista().txtModeloA.setText(datos[1].trim());
                    modelo.getVista().txtCapacidadA.setText(datos[2].trim());
                    return;
                }
            }

           

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar datos del autobús.");
        }
        
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modelo.getVista().btnAsignar) {
            asignarBusARuta();
        }
    }

    public void asignarBusARuta() {
        String idRuta = modelo.getVista().txtCodigoR.getText().trim();
        String placa = (String) modelo.getVista().cmbBusesA.getSelectedItem();

        if (idRuta.isEmpty() || placa == null) {
            JOptionPane.showMessageDialog(null, "Seleccione una ruta y un bus");
            return;
        }

        String modeloBus = modelo.getVista().txtModeloA.getText();
        String capacidad = modelo.getVista().txtCapacidadA.getText();
        String fechaAsignacion = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoAsignRuta, true))) {
            writer.write(idRuta + " | " + placa + " | " + modeloBus + " | " + capacidad + " | " + fechaAsignacion);
            writer.newLine();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar asignación");
            return;
        }

        actualizarEstadoRuta(idRuta);
        actualizarEstadoBus(placa);
        actualizarEstadoConductor(placa);

        JOptionPane.showMessageDialog(null, "Ruta asignada correctamente");
        cargarRutas();
        cargarBuses();
    }

    public void actualizarEstadoRuta(String idRuta) {
        File temp = new File("rutasTemp.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoRutas));
             BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos[0].trim().equals(idRuta)) {
                    datos[5] = "Asignada";
                }
                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar ruta");
        }
        archivoRutas.delete();
        temp.renameTo(archivoRutas);
    }

    public void actualizarEstadoBus(String placa) {
        File temp = new File("busesTemp.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoBuses));
             BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos[0].trim().equals(placa)) {
                    datos[3] = "Ruta Asignada";
                }
                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar estado del bus");
        }
        archivoBuses.delete();
        temp.renameTo(archivoBuses);
    }

   public void actualizarEstadoConductor(String placaBus) {
    String codigo = "";
    try (BufferedReader reader = new BufferedReader(new FileReader(archivoAsignaciones))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 3 && datos[2].trim().equals(placaBus)) {
                codigo = datos[0].trim();
                break;
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al buscar conductor asignado");
    }

    if (!codigo.isEmpty()) {
        File temp = new File("conductoresTemp.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader("conductores.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 8 && datos[0].trim().equals(codigo)) {
                    datos[7] = "Asignado";
                }
                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar conductor");
        }
        new File("conductores.txt").delete();
        temp.renameTo(new File("conductores.txt"));
    }
}

    public void desasignarBusDeRuta() {
    String idRuta = modelo.getVista().txtCodigoR.getText().trim();

    if (idRuta.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Seleccione una ruta para desasignar el bus.");
        return;
    }

    File archivoTemp = new File("asignacion_ruta_temp.txt");
    File archivoDesasignaciones = new File("desasignacion_buses.txt");
    boolean encontrado = false;
    String lineaEliminada = "";

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivoAsignRuta));
        BufferedWriter writer = new BufferedWriter(new FileWriter(archivoTemp));
        BufferedWriter writerDesasignacion = new BufferedWriter(new FileWriter(archivoDesasignaciones, true))
    ) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 2 && datos[0].trim().equals(idRuta)) {
                encontrado = true;
                lineaEliminada = linea.trim();
                // Guardar en desasignaciones con fecha
                String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                writerDesasignacion.write(linea.trim() + " | " + fecha);
                writerDesasignacion.newLine();
                continue;
            }
            writer.write(linea);
            writer.newLine();
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al procesar la desasignación de ruta.");
        return;
    }

    if (!encontrado) {
        JOptionPane.showMessageDialog(null, "No se encontró una asignación para esa ruta.");
        return;
    }

    if (archivoAsignRuta.delete() && archivoTemp.renameTo(archivoAsignRuta)) {
        String placaBus = obtenerPlacaDesdeAsignacion(lineaEliminada);
        actualizarEstadoRutaDesasignada(idRuta);
        actualizarEstadoBusDesasignado(placaBus);

        JOptionPane.showMessageDialog(null, "Bus desasignado correctamente de la ruta.");
        cargarRutas();
        cargarBuses();
    } else {
        JOptionPane.showMessageDialog(null, "Error al actualizar las asignaciones.");
    }
}
private String obtenerPlacaDesdeAsignacion(String linea) {
    String[] datos = linea.split("\\|");
    if (datos.length >= 2) {
        return datos[1].trim();
    }
    return "";
}

private void actualizarEstadoRutaDesasignada(String idRuta) {
    File temp = new File("rutas_temp.txt");
    try (BufferedReader reader = new BufferedReader(new FileReader(archivoRutas));
         BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 6 && datos[0].trim().equals(idRuta)) {
                datos[5] = "Activa";
            }
            writer.write(String.join(" | ", datos));
            writer.newLine();
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar estado de ruta.");
    }
    archivoRutas.delete();
    temp.renameTo(archivoRutas);
}

private void actualizarEstadoBusDesasignado(String placa) {
    File temp = new File("buses_temp.txt");
    try (BufferedReader reader = new BufferedReader(new FileReader(archivoBuses));
         BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 4 && datos[0].trim().equals(placa)) {
                datos[3] = "Asignado"; // Vuelve a estado de asignado, pero no está en ruta
            }
            writer.write(String.join(" | ", datos));
            writer.newLine();
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar estado del bus.");
    }
    archivoBuses.delete();
    temp.renameTo(archivoBuses);
}

}

