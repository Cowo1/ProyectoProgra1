/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.ModeloPrincipal;
import Vista.VistaBoletos;
import Vista.VistaBuses;
import Vista.VistaConductores;
import Vista.VistaConsultas;
import Vista.VistaManual;
import Vista.VistaPasajeros;
import Vista.VistaReportes;
import Vista.VistaReportesV;
import Vista.VistaRutas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author diego
 */
public class ControladorPrincipal implements ActionListener, MouseListener {

    public ControladorPrincipal(ModeloPrincipal modelo) {
        this.modelo = modelo;
        cargarRutas();
    }
    
    
    
    private ModeloPrincipal modelo;
    
    

    @Override
    public void actionPerformed(ActionEvent e) {
         if(e.getSource().equals(modelo.getVista().mntmAutobuses)){
            VistaBuses vBuses = new VistaBuses();
            vBuses.setVisible(true);
            modelo.getVista().dispose();
        }
        else if(e.getSource().equals(modelo.getVista().mntmRutas)){
            VistaRutas vRutas = new VistaRutas();
            vRutas.setVisible(true);
            modelo.getVista().dispose();
        }
        else if(e.getSource().equals(modelo.getVista().mntmConductores)){
            VistaConductores vConductores = new VistaConductores();
            vConductores.setVisible(true);
            modelo.getVista().dispose();
        }
        else if(e.getSource().equals(modelo.getVista().mntmPasajeros)){
            VistaPasajeros vPasajeros = new VistaPasajeros();
            vPasajeros.setVisible(true);
            modelo.getVista().dispose();
        }
        else if(e.getSource().equals(modelo.getVista().mntmReporteV)){
            VistaReportesV vReportesv = new VistaReportesV();
            vReportesv.setVisible(true);
            modelo.getVista().dispose();
        }
         else if(e.getSource().equals(modelo.getVista().mntmReporteC)){
            VistaReportes vReportesC = new VistaReportes();
            vReportesC.setVisible(true);
            modelo.getVista().dispose();
        }
        else if(e.getSource().equals(modelo.getVista().mntmManual)){
            VistaManual vManual = new VistaManual();
            vManual.setVisible(true);
            modelo.getVista().dispose();
        }
        else if(e.getSource().equals(modelo.getVista().mntmVenta)){
            VistaBoletos vBoletos = new VistaBoletos();
            vBoletos.setVisible(true);
            modelo.getVista().dispose();
        }
        else if(e.getSource().equals(modelo.getVista().mntmConsultas)){
            VistaConsultas vConsultas = new VistaConsultas();
            vConsultas.setVisible(true);
            modelo.getVista().dispose();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getComponent().equals(modelo.getVista().mntmAutobuses)){
            VistaBuses vBuses = new VistaBuses();
            vBuses.setVisible(true);
            modelo.getVista().dispose();
        }
        if(e.getComponent().equals(modelo.getVista().mntmRutas)){
            VistaRutas vRutas= new VistaRutas();
            vRutas.setVisible(true);
            modelo.getVista().dispose();
        }
        if(e.getComponent().equals(modelo.getVista().mntmConductores)){
            VistaConductores vConductores = new VistaConductores();
            vConductores.setVisible(true);
            modelo.getVista().dispose();
        }
        if(e.getComponent().equals(modelo.getVista().mntmPasajeros)){
            VistaPasajeros vPasajeros = new VistaPasajeros();
            vPasajeros.setVisible(true);
            modelo.getVista().dispose();
        }
      if(e.getComponent().equals(modelo.getVista().mntmReporteV)){
            VistaReportes vReportes = new VistaReportes();
            vReportes.setVisible(true);
            modelo.getVista().dispose();
        }
      if(e.getComponent().equals(modelo.getVista().mntmManual)){
            VistaManual vManual = new VistaManual();
            vManual.setVisible(true);
            modelo.getVista().dispose();
        }
      if(e.getComponent().equals(modelo.getVista().mntmVenta)){
            VistaBoletos vBoletos = new VistaBoletos();
            vBoletos.setVisible(true);
            modelo.getVista().dispose();
        }
      if(e.getComponent().equals(modelo.getVista().mntmConsultas)){
            VistaConsultas vConsultas = new VistaConsultas();
            vConsultas.setVisible(true);
            modelo.getVista().dispose();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public void cargarRutas() {
    DefaultTableModel model = (DefaultTableModel) modelo.getVista().tblResumen.getModel();
    model.setRowCount(0); // Limpiar tabla

    File archivo = new File("asignacion_ruta.txt");

    if (!archivo.exists()) {
        JOptionPane.showMessageDialog(null, "El archivo asignacion_ruta.txt no existe.");
        return;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
        String linea;
        while ((linea = br.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 9) {
                String codigoRuta = datos[0].trim();
                String origen = datos[1].trim();
                String destino = datos[2].trim();
                String horaSalida = datos[3].trim();
                String placaBus = datos[5].trim();
                String capacidad = datos[7].trim();

                model.addRow(new Object[]{
                    codigoRuta, origen, destino, horaSalida, placaBus, capacidad
                });
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al cargar resumen de rutas: " + e.getMessage());
    }
}


    
}
