/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.ModeloRutas;
import Vista.VistaRutas;
import Vista.VistaRutas;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author diego
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.core.parser.ParseException;

public class ControladorRutas implements ActionListener {
    private ModeloRutas modelo;
   File archivo = new File("rutas.txt");
  

    public ControladorRutas(ModeloRutas modelo) {
        this.modelo = modelo;
     

        modelo.getVista().btnAgregar.addActionListener(this);
      
        modelo.getVista().btnEliminar.addActionListener(this);
        modelo.getVista().btnBuscar.addActionListener(this);
        modelo.getVista().btnLimpiar.addActionListener(this);


        modelo.getVista().setVisible(true);
        cargarTabla();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modelo.getVista().btnAgregar) {
            guardarRuta();
            cargarTabla();
        }  else if (e.getSource() == modelo.getVista().btnEliminar) {
            eliminarRuta();
        } else if (e.getSource() == modelo.getVista().btnBuscar) {
            buscarRuta();
        } else if (e.getSource() == modelo.getVista().btnLimpiar) {
            limpiar();
        }
    }

   private void guardarRuta() {
    String id = modelo.getVista().txtIdRuta.getText().trim();

    if (id.isEmpty()) {
        JOptionPane.showMessageDialog(modelo.getVista(), "Ingrese el ID de la ruta.");
        return;
    }

    String origen = modelo.getVista().txtOrigen.getText().trim();
    String destino = modelo.getVista().txtDestino.getText().trim();
    String horario = modelo.getVista().txtHorario.getText().trim();
    String precio = modelo.getVista().txtPrecio.getText().trim();
    String estado = (String) modelo.getVista().cbRutas.getSelectedItem();

    
    if (horaP()) {
        estado = "Inactiva";
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
        writer.write(id + " | " + origen + " | " + destino + " | " + horario + " | " + precio + " | " + estado);
        writer.newLine();
        JOptionPane.showMessageDialog(null, "Ruta guardada correctamente");
        cargarTabla();
        limpiar();
     
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al guardar la ruta");
    }
}

    

    private void eliminarRuta() {
        String id = modelo.getVista().txtIdRuta.getText().trim();
if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el id para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File temporal = new File("temp_rutas.txt");

        try (
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            BufferedWriter writer = new BufferedWriter(new FileWriter(temporal));
        ) {
            String linea;
            boolean encontrado = false;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(" \\| ");
                if (datos.length > 0 && datos[0].equalsIgnoreCase(id)) {
                    encontrado = true;
                    continue;
                }
                writer.write(linea);
                writer.newLine();
            }
            } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar la ruta " + e.getMessage());
            return;
            }

           if (archivo.delete()) {
        if (temporal.renameTo(archivo)) {
            JOptionPane.showMessageDialog(null, "Ruta eliminada con exito.");
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(null, "Error al renombrar el archivo temporal.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Error al eliminar el archivo original.");
    }
       
    }

    private void buscarRuta() {
        String id = modelo.getVista().txtIdRuta.getText().trim();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean encontrado = false;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\| ");
               if (datos.length >= 6 && datos[0].trim().equalsIgnoreCase(id)){
                   modelo.getVista().txtOrigen.setText(datos[1].trim());
                    modelo.getVista().txtDestino.setText(datos[2].trim());
                    modelo.getVista().txtHorario.setText(datos[3].trim());
                      modelo.getVista().txtPrecio.setText(datos[4].trim());
                        encontrado = true;
                        break;
               }
    }
            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "Ruta no encontrada");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar la ruta");
        }
        }
            
            
       
    
            
    private void cargarTabla() {
         DefaultTableModel model = (DefaultTableModel) modelo.getVista().tblRutas.getModel();
        model.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if(datos.length == 6){
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

    private void limpiar() {
        modelo.getVista().txtIdRuta.setText("");
        modelo.getVista().txtOrigen.setText("");
        modelo.getVista().txtDestino.setText("");
        modelo.getVista().txtHorario.setText("");
        modelo.getVista().txtPrecio.setText("");
    }

    private boolean horaP() {
    String horaT = modelo.getVista().txtHorario.getText().trim();

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    sdf.setLenient(false); 

    try {
        Date horaRuta = sdf.parse(horaT); 

        Calendar horaA = Calendar.getInstance();
        Calendar horaS = Calendar.getInstance();
        horaS.setTime(horaRuta);

        
        horaS.set(Calendar.DAY_OF_MONTH, horaA.get(Calendar.DAY_OF_MONTH));
        horaS.set(Calendar.MONTH, horaA.get(Calendar.MONTH));
        horaS.set(Calendar.YEAR, horaA.get(Calendar.YEAR));

        return horaS.before(horaA); 
    }   catch (java.text.ParseException ex) {
            Logger.getLogger(ControladorRutas.class.getName()).log(Level.SEVERE, null, ex);
          return false;
    }
      
}

}

