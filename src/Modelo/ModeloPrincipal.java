/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Vista.VistaPrincipal;

/**
 *
 * @author diego
 */
public class ModeloPrincipal {
    private VistaPrincipal vista;

    public VistaPrincipal getVista() {
        return vista;
    }

    public void setVista(VistaPrincipal vista) {
        this.vista = vista;
    }

    public ModeloPrincipal(VistaPrincipal vista) {
        this.vista = vista;
    }
    
    public ModeloPrincipal(){
        
    }
    
    
}
