/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author diego
 */
import Modelo.ModeloConsultas;
import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ControladorConsultas implements ActionListener {

    private ModeloConsultas modelo;
    private File archivoRutas = new File("rutas.txt");
    private File archivoAsignaciones = new File("asignacion_ruta.txt");

    public ControladorConsultas(ModeloConsultas modelo) {
        this.modelo = modelo;
        cargarRutas();

        modelo.getVista().cmbRuta.addActionListener(this);
    }

    // Cargar rutas asignadas al ComboBox
    private void cargarRutas() {
        modelo.getVista().cmbRuta.removeAllItems();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoRutas))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 6 && datos[5].trim().equalsIgnoreCase("Asignada") || datos[5].trim().equalsIgnoreCase("Ocupada")) {
                    modelo.getVista().cmbRuta.addItem(datos[1].trim());  // Origen
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar rutas disponibles");
        }
    }

    // Mostrar datos en tabla al seleccionar una ruta
    private void mostrarDatosRuta(String origenSeleccionado) {
        try (
                BufferedReader readerRuta = new BufferedReader(new FileReader(archivoRutas)); BufferedReader readerAsignacion = new BufferedReader(new FileReader(archivoAsignaciones))) {
            String idRuta = "", destino = "", horario = "", precio = "", estado = "";

            String linea;
            while ((linea = readerRuta.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 6 && datos[1].trim().equals(origenSeleccionado)) {
                    idRuta = datos[0].trim();
                    destino = datos[2].trim();
                    horario = datos[3].trim();
                    precio = datos[4].trim();
                    estado = datos[5].trim();

                    modelo.getVista().txtDestino.setText(destino);
                    modelo.getVista().txtHorario.setText(horario);

                    if (estado.equalsIgnoreCase("Asignada")) {
                        modelo.getVista().lblEstadoRuta.setText("Disponible");
                        modelo.getVista().lblEstadoRuta.setForeground(new Color(0, 128, 0));
                    } else if (estado.equalsIgnoreCase("Ocupada")) {
                        modelo.getVista().lblEstadoRuta.setText("No hay disponibilidad");
                        modelo.getVista().lblEstadoRuta.setForeground(Color.RED);
                    } else {
                        modelo.getVista().lblEstadoRuta.setText("Estado desconocido");
                        modelo.getVista().lblEstadoRuta.setForeground(Color.GRAY);
                    }
                    break;
                }
            }

            while ((linea = readerAsignacion.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 9 && datos[0].trim().equals(idRuta)) {
                    String origen = datos[1].trim();
                    String placa = datos[5].trim();
                    String modeloBus = datos[6].trim();
                    String capacidad = datos[7].trim();
                    String fechaAsignacion = datos[8].trim();

                    modelo.getVista().txtPlacaBus.setText(placa);
                    modelo.getVista().txtAsientosDisponibles.setText(capacidad);

                    DefaultTableModel model = (DefaultTableModel) modelo.getVista().tblConsultaRuta.getModel();
                    model.setRowCount(0);
                    model.addRow(new Object[]{
                        idRuta, origen, destino, horario, precio,
                        placa, modeloBus, capacidad, fechaAsignacion
                    });

                    return;
                }
            }

            modelo.getVista().txtPlacaBus.setText("");
            modelo.getVista().txtAsientosDisponibles.setText("");
            JOptionPane.showMessageDialog(null, "No se encontró un bus asignado a esta ruta.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar los datos.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modelo.getVista().cmbRuta) {
            String seleccion = modelo.getVista().cmbRuta.getSelectedItem().toString();
            mostrarDatosRuta(seleccion);
        }
    }
}
