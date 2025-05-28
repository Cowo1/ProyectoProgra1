/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Vista.VistaReportes;

/**
 *
 * @author diego
 */
public class ModeloReportes {
    private VistaReportes vista;

    public ModeloReportes() {
    }

    public ModeloReportes(VistaReportes vista) {
        this.vista = vista;
    }

    public VistaReportes getVista() {
        return vista;
    }

    public void setVista(VistaReportes vista) {
        this.vista = vista;
    }
    
}
