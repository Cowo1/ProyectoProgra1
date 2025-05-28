/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Vista.VistaAsignarBus;

/**
 *
 * @author diego
 */
public class ModeloAsignarBus {
    private VistaAsignarBus vista;

    public ModeloAsignarBus(VistaAsignarBus vista) {
        this.vista = vista;
    }

    public VistaAsignarBus getVista() {
        return vista;
    }

    public void setVista(VistaAsignarBus vista) {
        this.vista = vista;
    }
    
    
}
