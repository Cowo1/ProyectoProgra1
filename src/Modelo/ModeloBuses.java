/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Vista.VistaBuses;

/**
 *
 * @author diego
 */
public class ModeloBuses {

    private VistaBuses vista;

    
    private String placa;
    private String modelo;
    private int capacidad;
    private String estado;
    
        public ModeloBuses() {
    }

    public ModeloBuses(String placa, String modelo, int capacidad, String estado) {
        this.placa = placa;
        this.modelo = modelo;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public ModeloBuses(VistaBuses vista) {
        this.vista = vista;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
        public VistaBuses getVista() {
        return vista;
    }

    public void setVista(VistaBuses vista) {
        this.vista = vista;
    }
   
    
}
