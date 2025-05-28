/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.ModeloBoletos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

public class ControladorBoletos implements ActionListener {

    private ModeloBoletos modelo;
    private File archivoRutas = new File("rutas.txt");
    private File archivoVentas = new File("ventas.txt");
    private File archivoPasajeros = new File("pasajeros.txt");
    private File archivoBuses = new File("buses.txt");
    private File archivoConductores = new File("conductores.txt");

    public ControladorBoletos(ModeloBoletos modelo) {
        this.modelo = modelo;

        cargarRutas();

        modelo.getVista().cmbRuta.addActionListener(this);
        modelo.getVista().btnRegistrarVenta.addActionListener(this);
    }

    private void cargarRutas() {
        modelo.getVista().cmbRuta.removeAllItems();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoRutas))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 6 && datos[5].trim().equalsIgnoreCase("Asignada")) {
                    modelo.getVista().cmbRuta.addItem(datos[1].trim());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar rutas disponibles");
        }
    }

    private void mostrarDatosRuta(String origenSeleccionado) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoRutas))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 6 && datos[1].trim().equals(origenSeleccionado)) {
                    modelo.getVista().txtCodigoRuta.setText(datos[0].trim());
                    modelo.getVista().txtDestino.setText(datos[2].trim());
                    modelo.getVista().txtHorario.setText(datos[3].trim());
                    modelo.getVista().txtPrecio.setText(datos[4].trim());

                    mostrarDatosBusYAsientos(datos[0].trim());
                    return;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar datos de la ruta");
        }
    }

   private void mostrarDatosBusYAsientos(String codigoRuta) {
    try (BufferedReader reader = new BufferedReader(new FileReader("asignacion_ruta.txt"))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\s*\\|\\s*"); // Maneja espacios alrededor del separador

            if (datos.length >= 5 && datos[0].trim().equals(codigoRuta)) {
                String placaBus = datos[1].trim();
                String modeloBus = datos[2].trim();
                String capacidad = datos[3].trim();

                modelo.getVista().txtPlaca.setText(placaBus);
                modelo.getVista().txtAsientos.setText(capacidad);
                // Si tienes campos para modelo o fecha asignación, también puedes mostrarlo aquí
                return;
            }
        }

        // Si no encontró la asignación
        modelo.getVista().txtPlaca.setText("");
        modelo.getVista().txtAsientos.setText("");
        JOptionPane.showMessageDialog(null, "No se encontró un bus asignado a esta ruta.");

    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al leer asignación de bus a ruta.");
    }
}


    private void registrarVenta() {
        String nombre = modelo.getVista().txtNombre.getText();
        String dpi = modelo.getVista().txtDPI.getText();
        String telefono = modelo.getVista().txtTelefono.getText();

        String codigoRuta = modelo.getVista().txtCodigoRuta.getText();
        String nombreRuta = modelo.getVista().cmbRuta.getSelectedItem().toString();
        String destino = modelo.getVista().txtDestino.getText();
        String horario = modelo.getVista().txtHorario.getText();
        String precio = modelo.getVista().txtPrecio.getText();
        String placaBus = modelo.getVista().txtPlaca.getText();

        Date fechaViaje = modelo.getVista().dcFecha.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaFormateada = sdf.format(fechaViaje);

        int asientos = Integer.parseInt(modelo.getVista().txtAsientos.getText());

        if (asientos <= 0) {
            JOptionPane.showMessageDialog(null, "No hay asientos disponibles en esta ruta.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoVentas, true))) {
            writer.write(nombreRuta + " | " + codigoRuta + " | " + destino + " | " + horario + " | " + precio + " | " + placaBus + " | " + nombre + " | " + dpi + " | " + telefono + " | " + fechaFormateada);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar la venta");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoPasajeros, true))) {
            writer.write(nombre + " | " + dpi + " | " + telefono);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar pasajero");
        }

        actualizarAsientos(placaBus, asientos - 1);
        actualizarConductorYRuta(placaBus, codigoRuta);

        JOptionPane.showMessageDialog(null, "Venta registrada correctamente");
    }

    private void actualizarAsientos(String placaBus, int nuevoValor) {
        File temp = new File("temp_buses.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoBuses));
             BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 4 && datos[0].trim().equals(placaBus)) {
                    datos[2] = String.valueOf(nuevoValor);
                    if (nuevoValor == 0) datos[3] = "En ruta";
                }
                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar capacidad del bus");
        }

        archivoBuses.delete();
        temp.renameTo(archivoBuses);
    }

    private void actualizarConductorYRuta(String placaBus, String codigoRuta) {
        // Actualizar conductor
        File tempConductores = new File("temp_conductores.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoConductores));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempConductores))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 8 && datos[7].trim().equals("Asignado")) {
                    datos[7] = "En ruta";
                }
                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar estado del conductor");
        }
        archivoConductores.delete();
        tempConductores.renameTo(archivoConductores);

        // Actualizar ruta
        File tempRutas = new File("temp_rutas.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoRutas));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempRutas))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 6 && datos[0].trim().equals(codigoRuta)) {
                    datos[5] = "En ruta";
                }
                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar estado de la ruta");
        }
        archivoRutas.delete();
        tempRutas.renameTo(archivoRutas);
    }
    private void actualizarEstadoRuta(String codigoRuta) {
    File archivoOriginal = new File("rutas.txt");
    File archivoTemporal = new File("rutas_temp.txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(archivoOriginal));
         BufferedWriter writer = new BufferedWriter(new FileWriter(archivoTemporal))) {

        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\s*\\|\\s*");

            if (datos.length >= 6 && datos[0].trim().equals(codigoRuta)) {
                datos[5] = "Ocupada"; // Cambia el estado
            }

            writer.write(String.join(" | ", datos));
            writer.newLine();
        }

    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar estado de la ruta.");
        return;
    }

    archivoOriginal.delete();
    archivoTemporal.renameTo(archivoOriginal);
}


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modelo.getVista().cmbRuta) {
            String seleccion = modelo.getVista().cmbRuta.getSelectedItem().toString();
            mostrarDatosRuta(seleccion);
        } else if (e.getSource() == modelo.getVista().btnRegistrarVenta) {
            registrarVenta();
        }
    }
}


