/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author diego
 */

import Modelo.ModeloManual;
import Vista.VistaManual;



import Modelo.ModeloManual;
import Vista.VistaManual;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import javax.swing.JOptionPane;

import javax.swing.JOptionPane;

public class ControladorManual implements ActionListener {

    private ModeloManual modelo;

    public ControladorManual(ModeloManual modelo) {
        this.modelo = modelo;

        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modelo.getVista().btnAbrir) {
            abrirManual();
        }

        if (e.getSource() == modelo.getVista().btnDescargar) {
            descargarManual();
        }
    }
//Metodo para abrir el manual automaticamentre
    private void abrirManual() {
        try {
            File archivo = new File(modelo.getRutaManual());
            if (archivo.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(archivo);
                } else {
                    JOptionPane.showMessageDialog(modelo.getVista(),
                            "La apertura automática no es soportada en este sistema.");
                }
            } else {
                JOptionPane.showMessageDialog(modelo.getVista(),
                        "El archivo del manual no se encuentra.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(modelo.getVista(),
                    "Error al abrir el manual: " + ex.getMessage());
        }
    }
//Elejor donde descargar el manual y descargarlo
    private void descargarManual() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("manual.pdf"));
            int opcion = fileChooser.showSaveDialog(modelo.getVista());

            if (opcion == JFileChooser.APPROVE_OPTION) {
                File destino = fileChooser.getSelectedFile();
                Path origen = Paths.get(modelo.getRutaManual());
                Files.copy(origen, destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(modelo.getVista(), "Manual guardado con éxito.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(modelo.getVista(),
                    "Error al guardar el manual: " + ex.getMessage());
        }
    }
}


