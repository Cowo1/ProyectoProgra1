/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author diego
 */
import Vista.VistaRutas;


public class ModeloRutas  {
    private VistaRutas vista;
    private String idRuta;
    private String origen;
    private String destino;
    private String horario;
    private String precio;
    private String estado;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
    public ModeloRutas(String estado) {
        this.estado = estado;
    }

    public ModeloRutas() {}

    public ModeloRutas(VistaRutas vista) {
        this.vista = vista;
    }

    public VistaRutas getVista() {
        return vista;
    }

    public void setVista(VistaRutas vista) {
        this.vista = vista;
    }

    
    public ModeloRutas(String idRuta, String origen, String destino, String horario, String precio) {
        this.idRuta = idRuta;
        this.origen = origen;
        this.destino = destino;
        this.horario = horario;
        this.precio = precio;
    }

   

   
    
     public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }  

}
