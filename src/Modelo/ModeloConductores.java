/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Vista.VistaConductores;

/**
 *
 * @author diego
 */
public class ModeloConductores {
    public ModeloConductores(){
        
    }
    
    private VistaConductores vista;
    private int codigo;
    private int DPI;
    private String nombre;
    private String telefono;
    private String direccion;
    private int licencia;
    private String tipo;
    private String vencimiento;
    private String ingreso;
    private String estado;

    public ModeloConductores(VistaConductores vista, int codigo, int DPI, String nombre, String telefono, String direccion, int licencia, String tipo, String vencimiento, String ingreso, String estado) {
        this.vista = vista;
        this.codigo = codigo;
        this.DPI = DPI;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.licencia = licencia;
        this.tipo = tipo;
        this.vencimiento = vencimiento;
        this.ingreso = ingreso;
        this.estado = estado;
    }

    public ModeloConductores(VistaConductores vista) {
        this.vista = vista;
    }

    

    public VistaConductores getVista() {
        return vista;
    }

    public void setVista(VistaConductores vista) {
        this.vista = vista;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getDPI() {
        return DPI;
    }

    public void setDPI(int DPI) {
        this.DPI = DPI;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getLicencia() {
        return licencia;
    }

    public void setLicencia(int licencia) {
        this.licencia = licencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(String vencimiento) {
        this.vencimiento = vencimiento;
    }

    public String getIngreso() {
        return ingreso;
    }

    public void setIngreso(String ingreso) {
        this.ingreso = ingreso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
