/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.ModeloRutas;


/**
 *
 * @author diego
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

// CONTROLADOR ACTUALIZADO COMPLETO

public class ControladorRutas implements ActionListener {

    private ModeloRutas modelo;
    File archivo = new File("rutas.txt");
    File archivoAsignacionRuta = new File("asignacion_ruta.txt");

    public ControladorRutas(ModeloRutas modelo) {
        this.modelo = modelo;
        verificarHorariosYActualizar(); 
        cargarTabla();
        modelo.getVista().btnAgregar.addActionListener(this);
        modelo.getVista().btnEliminar.addActionListener(this);
        modelo.getVista().btnEliminarF.addActionListener(this);
        modelo.getVista().btnBuscar.addActionListener(this);
        modelo.getVista().btnLimpiar.addActionListener(this);
        modelo.getVista().tblRutas.getSelectionModel().addListSelectionListener(e -> seleccionarFila());

        modelo.getVista().setVisible(true);

        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modelo.getVista().btnAgregar) {
            guardarRuta();
            cargarTabla();
        } else if (e.getSource() == modelo.getVista().btnEliminar) {
            eliminarRuta();
        } else if (e.getSource() == modelo.getVista().btnBuscar) {
            buscarRuta();
        } else if (e.getSource() == modelo.getVista().btnLimpiar) {
            limpiar();
        }else if (e.getSource() == modelo.getVista().btnEliminarF) {
            eliminarFila();
        }
    }

   private void guardarRuta() {
    String id = modelo.getVista().txtIdRuta.getText().trim();
    if (!id.matches("\\d+")) {
        JOptionPane.showMessageDialog(null, "El ID debe ser un número entero.");
        return;
    }

    String origen = modelo.getVista().txtOrigen.getText().trim();
    String destino = modelo.getVista().txtDestino.getText().trim();
    String horario = modelo.getVista().txtHorario.getText().trim();
    String precio = modelo.getVista().txtPrecio.getText().trim();
    String estado = (String) modelo.getVista().cbRutas.getSelectedItem();
    String frecuencia = (String) modelo.getVista().cbFrecuencia.getSelectedItem();

    if (!horario.matches("[0-2]?[0-9]:[0-5][0-9]")) {
        JOptionPane.showMessageDialog(null, "Formato de horario incorrecto. Use HH:mm en formato 24 horas.");
        return;
    }

    if (horaPasada(horario)) {
    switch (estado) {
        case "Activa":
            estado = "Inactiva";
            break;
        case "Asignada":
            estado = "Ocupada";
            break;
        case "Disponible":
            estado = "Inactiva";
            break;
        default:
            // Para cualquier otro estado (como "En ruta", etc.), puedes decidir si mantenerlo o cambiarlo
            break;
    }
}

    // Verificar si ya existe una ruta con ese ID
    boolean existe = false;
    File archivoTemporal = new File("rutas_temp.txt");

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        BufferedWriter writer = new BufferedWriter(new FileWriter(archivoTemporal))
    ) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 1 && datos[0].trim().equals(id)) {
                existe = true;
                int opcion = JOptionPane.showConfirmDialog(null,
                        "Ya existe una ruta con este código. ¿Desea sobrescribirla?",
                        "Confirmar sobreescritura",
                        JOptionPane.YES_NO_OPTION);

                if (opcion == JOptionPane.NO_OPTION) {
                    writer.write(linea); // mantener la antigua
                    writer.newLine();
                    seguirProcesoDespuesDeGuardar(false); // salir sin guardar
                    return;
                }

                // Si desea sobrescribir, no escribir la línea antigua
                continue;
            }
            writer.write(linea);
            writer.newLine();
        }

        // Agregar la nueva o actualizada ruta
        writer.write(id + " | " + origen + " | " + destino + " | " + horario + " | " + precio + " | " + estado + " | " + frecuencia);
        writer.newLine();

    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al guardar la ruta: " + e.getMessage());
        return;
    }

    // Reemplazar archivo original
    if (archivo.delete() && archivoTemporal.renameTo(archivo)) {
        JOptionPane.showMessageDialog(null, existe ? "Ruta actualizada correctamente" : "Ruta guardada correctamente");
        seguirProcesoDespuesDeGuardar(true);
    } else {
        JOptionPane.showMessageDialog(null, "Error al actualizar el archivo.");
    }
}

private void seguirProcesoDespuesDeGuardar(boolean continuar) {
    if (continuar) {
        cargarTabla();
        limpiar();
    }
}


    private void eliminarRuta() {
        int fila = modelo.getVista().tblRutas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
            return;
        }

        String id = modelo.getVista().tblRutas.getValueAt(fila, 0).toString();

        if (tieneBusAsignado(id)) {
            JOptionPane.showMessageDialog(null, "No se puede eliminar una ruta con bus asignado.");
            return;
        }

        File temporal = new File("temp_rutas.txt");
        try (
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            BufferedWriter writer = new BufferedWriter(new FileWriter(temporal));
        ) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (!datos[0].trim().equals(id)) {
                    writer.write(linea);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar la ruta: " + e.getMessage());
            return;
        }

        archivo.delete();
        temporal.renameTo(archivo);

        JOptionPane.showMessageDialog(null, "Ruta eliminada con éxito.");
        cargarTabla();
        limpiar();
    }
public void eliminarFila() {
    int filaS = modelo.getVista().tblRutas.getSelectedRow();
    
    if (filaS == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
        return;
    }

    String codigoEliminar = modelo.getVista().tblRutas.getValueAt(filaS, 0).toString();

    // Verificar si tiene bus asignado
    if (tieneBusAsignado(codigoEliminar)) {
        JOptionPane.showMessageDialog(null, "Esta ruta tiene un bus asignado. Debe desasignarlo antes de poder eliminarla.");
        return;
    }

    // Proceder con la eliminación
    File archivoTemporal = new File("rutas_temp.txt");
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
                continue; // No escribir esta línea
            }

            writer.write(linea);
            writer.newLine();
        }

    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al eliminar la ruta.");
        return;
    }

    if (archivo.delete() && archivoTemporal.renameTo(archivo)) {
        if (eliminado) {
            JOptionPane.showMessageDialog(null, "Ruta eliminada correctamente.");
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró la ruta.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Error al reemplazar el archivo.");
    }
}

    private boolean tieneBusAsignado(String codigoRuta) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoAsignacionRuta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 2 && datos[0].trim().equals(codigoRuta)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    private void buscarRuta() {
        String id = modelo.getVista().txtIdRuta.getText().trim();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 7 && datos[0].trim().equalsIgnoreCase(id)) {
                    modelo.getVista().txtOrigen.setText(datos[1].trim());
                    modelo.getVista().txtDestino.setText(datos[2].trim());
                    modelo.getVista().txtHorario.setText(datos[3].trim());
                    modelo.getVista().txtPrecio.setText(datos[4].trim());
                    modelo.getVista().cbRutas.setSelectedItem(datos[5].trim());
                    modelo.getVista().cbFrecuencia.setSelectedItem(datos[6].trim());
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "Ruta no encontrada");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar la ruta");
        }
    }

    public void cargarTabla() {
         verificarHorariosYActualizar();
        DefaultTableModel model = (DefaultTableModel) modelo.getVista().tblRutas.getModel();
        model.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length == 7) {
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

    private void seleccionarFila() {
        int fila = modelo.getVista().tblRutas.getSelectedRow();
        if (fila != -1) {
            modelo.getVista().txtIdRuta.setText(modelo.getVista().tblRutas.getValueAt(fila, 0).toString());
            modelo.getVista().txtOrigen.setText(modelo.getVista().tblRutas.getValueAt(fila, 1).toString());
            modelo.getVista().txtDestino.setText(modelo.getVista().tblRutas.getValueAt(fila, 2).toString());
            modelo.getVista().txtHorario.setText(modelo.getVista().tblRutas.getValueAt(fila, 3).toString());
            modelo.getVista().txtPrecio.setText(modelo.getVista().tblRutas.getValueAt(fila, 4).toString());
            modelo.getVista().cbRutas.setSelectedItem(modelo.getVista().tblRutas.getValueAt(fila, 5).toString());
            modelo.getVista().cbFrecuencia.setSelectedItem(modelo.getVista().tblRutas.getValueAt(fila, 6).toString());
        }
    }

    private void limpiar() {
        modelo.getVista().txtIdRuta.setText("");
        modelo.getVista().txtOrigen.setText("");
        modelo.getVista().txtDestino.setText("");
        modelo.getVista().txtHorario.setText("");
        modelo.getVista().txtPrecio.setText("");
        modelo.getVista().cbRutas.setSelectedIndex(0);
        modelo.getVista().cbFrecuencia.setSelectedIndex(0);
    }

    private boolean horaPasada(String horaT) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setLenient(false);
        try {
            Date horaRuta = sdf.parse(horaT);
            Calendar horaActual = Calendar.getInstance();
            Calendar horaRutaCalendar = Calendar.getInstance();
            horaRutaCalendar.setTime(horaRuta);

            horaRutaCalendar.set(Calendar.YEAR, horaActual.get(Calendar.YEAR));
            horaRutaCalendar.set(Calendar.MONTH, horaActual.get(Calendar.MONTH));
            horaRutaCalendar.set(Calendar.DAY_OF_MONTH, horaActual.get(Calendar.DAY_OF_MONTH));

            return horaRutaCalendar.before(horaActual);
        } catch (ParseException ex) {
            return false;
        }
    }



public void verificarHorariosYActualizar() {
    File archivo = new File("rutas.txt");
    File temporal = new File("temp_rutas.txt");

    try (
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temporal))
    ) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");

            if (datos.length == 7) {
                for (int i = 0; i < datos.length; i++) {
                    datos[i] = datos[i].trim();
                }

                String codigoRuta = datos[0];
                String estado = datos[5];
                String horario = datos[3];

                if (horaPasada(horario)) {
                    if (estado.equalsIgnoreCase("Disponible") || estado.equalsIgnoreCase("Activa")) {
                        datos[5] = "Inactiva";
                    } else if (estado.equalsIgnoreCase("Asignada")) {
                        datos[5] = "Ocupada";

                        
                    }
                }

                writer.write(String.join(" | ", datos));
                writer.newLine();
            }
        }

    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar horarios vencidos");
    }

    archivo.delete();
    temporal.renameTo(archivo);
}




private String obtenerPlacaBusPorRuta(String codigoRuta) {
    File archivoAsignaciones = new File("asignacion_ruta.txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(archivoAsignaciones))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 6 && datos[0].trim().equalsIgnoreCase(codigoRuta)) {
                return datos[5].trim(); // Placa del bus está en la posición 6° (índice 5)
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al buscar la placa del bus por ruta");
    }

    return null;
}










}


