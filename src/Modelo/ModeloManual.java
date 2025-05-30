/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author diego
 */


import Vista.VistaManual;

public class ModeloManual {
    private VistaManual vista;
    private final String rutaManual = "docs/manual.pdf";

    public ModeloManual(VistaManual vista) {
        this.vista = vista;
    }

    public VistaManual getVista() {
        return vista;
    }

    public String getRutaManual() {
        return rutaManual;
    }
}

