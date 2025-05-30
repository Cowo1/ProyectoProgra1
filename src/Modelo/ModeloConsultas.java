/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Vista.VistaConsultas;

/**
 *
 * @author diego
 */
public class ModeloConsultas {
     private VistaConsultas vista;

    public ModeloConsultas(VistaConsultas vista) {
        this.vista = vista;
    }

    public VistaConsultas getVista() {
        return vista;
    }

    public void setVista(VistaConsultas vista) {
        this.vista = vista;
    }
}
