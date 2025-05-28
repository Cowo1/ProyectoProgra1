/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Vista.VistaAsignarConductor;

/**
 *
 * @author diego
 */
public class ModeloAsignacion {
    private VistaAsignarConductor vista;
  
    
    public ModeloAsignacion(){
        
    }

    public ModeloAsignacion(VistaAsignarConductor vista) {
        this.vista = vista;
    }

    

    public VistaAsignarConductor getVista() {
        return vista;
    }

    public void setVista(VistaAsignarConductor vista) {
        this.vista = vista;
    }
    
}

