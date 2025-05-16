/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Arvhivos;

import Modelo.ModeloBuses;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author diego
 */
public class ArchivoBuses  {
    private static final String archivo = "buses.txt";
    private static final String separador = " | ";
    private static final String encabezado = "Placa | Modelo | Capacidad | Estado";
    
    public boolean insertarBuses(ModeloBuses modelo){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))){
          if(new File(archivo).length() == 0)  {
              writer.write("placa, modelo, capacidad, estado");
              writer.newLine();
          }
          writer.write(modelo.getPlaca() + separador + 
                  modelo.getModelo()+ separador + 
                  modelo.getCapacidad() +    separador +
                 modelo.getEstado());
                 writer.newLine();
                 return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
            
            
    }

   
   
}
