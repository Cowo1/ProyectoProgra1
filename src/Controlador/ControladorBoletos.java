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
import java.util.Arrays;
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

    }
// Carga las rutas a un combobox
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
// Al seleccionar una ruta del combobox muestra los datos automaticamente
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
// Muestra los datos del bus cuando se selecciona su ruta asignada
    private void mostrarDatosBusYAsientos(String codigoRuta) {
        try (BufferedReader reader = new BufferedReader(new FileReader("asignacion_ruta.txt"))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\s*\\|\\s*");

                if (datos.length >= 8 && datos[0].trim().equals(codigoRuta)) {
                    String placaBus = datos[5].trim();
                    String capacidad = datos[7].trim();

                    modelo.getVista().txtPlaca.setText(placaBus);
                    modelo.getVista().txtAsientos.setText(capacidad);
                    return;
                }
            }

            modelo.getVista().txtPlaca.setText("");
            modelo.getVista().txtAsientos.setText("");
            JOptionPane.showMessageDialog(null, "No se encontró un bus asignado a esta ruta.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer asignación de bus a ruta.");
        }
    }
//Registra la venta
    private void registrarVenta() {
        String nombre = modelo.getVista().txtNombre.getText().trim();
        String dpi = modelo.getVista().txtDPI.getText().trim();
        String telefono = modelo.getVista().txtTelefono.getText().trim();

        String codigoRuta = modelo.getVista().txtCodigoRuta.getText().trim();
        String nombreRuta = modelo.getVista().cmbRuta.getSelectedItem().toString().trim();
        String destino = modelo.getVista().txtDestino.getText().trim();
        String horario = modelo.getVista().txtHorario.getText().trim();
        String precio = modelo.getVista().txtPrecio.getText().trim();
        String placaBus = modelo.getVista().txtPlaca.getText().trim();

        Date fechaViaje = modelo.getVista().dcFecha.getDate();
        if (fechaViaje == null) {
            JOptionPane.showMessageDialog(null, "Seleccione una fecha de viaje.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaFormateada = sdf.format(fechaViaje);
        if (dpi.length() != 13 || !dpi.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "El DPI debe contener exactamente 13 dígitos numéricos.");
            return;
        }
        // Validación de capacidad
        int asientos;
        try {
            asientos = Integer.parseInt(modelo.getVista().txtAsientos.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "La capacidad del bus no es válida.");
            return;
        }

        int nuevoAsiento = asientos - 1;
        if (nuevoAsiento < 0) {
            JOptionPane.showMessageDialog(null, "No hay asientos disponibles en esta ruta.");
            return;
        }

        // Registrar venta
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoVentas, true))) {
            writer.write(codigoRuta + " | " + nombreRuta + " | " + destino + " | " + horario + " | " + precio + " | " + placaBus + " | " + fechaFormateada + " | " + nombre + " | " + dpi + " | " + telefono);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar la venta");
            return;
        }

        // Registrar pasajero si no existe
        if (!pasajeroYaRegistrado(dpi)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoPasajeros, true))) {
                writer.write(nombre + " | " + dpi + " | " + telefono + " | " + fechaFormateada);
                writer.newLine();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al guardar pasajero");
            }
        }
        
        if (!rutaAsignada(codigoRuta)) {
            JOptionPane.showMessageDialog(null, "Esta ruta ya no está disponible para ventas.");
            cargarRutas(); 
            return;
        }

        
        actualizarAsientos(placaBus, nuevoAsiento);
        actualizarConductorYRuta(placaBus, codigoRuta, nuevoAsiento); 
        actualizarCapacidadAsignacion(placaBus, nuevoAsiento);
        JOptionPane.showMessageDialog(null, "Venta registrada correctamente");
        limpiarCampos();
        cargarRutas();
    }
//Metodo para tomar las rutas con su estado asignada
    private boolean rutaAsignada(String codigoRuta) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoRutas))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 6 && datos[0].trim().equals(codigoRuta)) {
                    return datos[5].trim().equalsIgnoreCase("Asignada");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar estado de la ruta");
        }
        return false;
    }
//Metodo para restar un asiento al bus cuando se realiza una venta
    private void actualizarCapacidadAsignacion(String placaBus, int nuevaCapacidad) {
        File archivoAsignacion = new File("asignacion_ruta.txt");
        File temp = new File("temp_asignacion.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoAsignacion)); BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 9 && datos[5].trim().equals(placaBus)) {
                    datos[7] = String.valueOf(nuevaCapacidad); // capacidad en posición 7
                }
                writer.write(String.join(" | ", Arrays.stream(datos).map(String::trim).toArray(String[]::new)));
                writer.newLine();
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar asignación de bus");
        }

        archivoAsignacion.delete();
        temp.renameTo(archivoAsignacion);
    }

    private void limpiarCampos() {
        modelo.getVista().txtNombre.setText("");
        modelo.getVista().txtDPI.setText("");
        modelo.getVista().txtTelefono.setText("");
        modelo.getVista().dcFecha.setDate(null);

        modelo.getVista().txtCodigoRuta.setText("");
        modelo.getVista().txtDestino.setText("");
        modelo.getVista().txtHorario.setText("");
        modelo.getVista().txtPrecio.setText("");
        modelo.getVista().txtPlaca.setText("");
        modelo.getVista().txtAsientos.setText("");
        modelo.getVista().cmbRuta.setSelectedIndex(-1);
    }
//Actualiza asientos luego de una venta
    private void actualizarAsientos(String placaBus, int nuevoValor) {
        File temp = new File("temp_buses.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoBuses)); BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 4 && datos[0].trim().equals(placaBus)) {
                    datos[2] = String.valueOf(nuevoValor);
                    if (nuevoValor == 0) {
                        datos[3] = "En ruta";
                    }
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
//Actualiza el estado de conductor y ruta cuando el tiempo de salida del bus ya llego o cuando alcanza su capacidad
    private void actualizarConductorYRuta(String placaBus, String codigoRuta, int asientosRestantes) {
        File tempConductores = new File("temp_conductores.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoConductores)); BufferedWriter writer = new BufferedWriter(new FileWriter(tempConductores))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (asientosRestantes == 0 && datos.length >= 8 && datos[7].trim().equals("Asignado")) {
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

        File tempRutas = new File("temp_rutas.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoRutas)); BufferedWriter writer = new BufferedWriter(new FileWriter(tempRutas))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 6 && datos[0].trim().equals(codigoRuta)) {
                    if (asientosRestantes == 0) {
                        datos[5] = "En ruta";
                    }
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
//Verifica el pasajero si ya fue registrado o no
    private boolean pasajeroYaRegistrado(String dpi) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoPasajeros))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 2 && datos[1].trim().equals(dpi)) {
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer archivo de pasajeros");
        }
        return false;
    }
//Acciones de la ventana
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modelo.getVista().cmbRuta) {
            Object seleccion = modelo.getVista().cmbRuta.getSelectedItem();
            if (seleccion != null) {
                mostrarDatosRuta(seleccion.toString());
            }
        } else if (e.getSource() == modelo.getVista().btnRegistrarVenta) {
            registrarVenta();
        }
    }
}
