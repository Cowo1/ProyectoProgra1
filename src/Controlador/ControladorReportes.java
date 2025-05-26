/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.ModeloReportes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.System.Logger;
import java.text.SimpleDateFormat;
import java.util.Date; 
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author diego
 */
public class ControladorReportes implements ActionListener {
 private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ControladorReportes.class);
    private ModeloReportes modelo;

    private File archivoConductores = new File("conductores.txt");

    public ControladorReportes(ModeloReportes modelo) {
        this.modelo = modelo;
        cargarTabla();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == modelo.getVista().btnGenerar){
            exportarExcel();
        }
    }


    public void cargarTabla() {
        DefaultTableModel model = new DefaultTableModel();
String[] columnas = {
    "Código", "Nombre", "DPI", "Teléfono", "Licencia", "Dirección", 
    "Tipo Licencia", "Estado", "Fecha Ingreso", "Fecha Vencimiento"
};
model.setColumnIdentifiers(columnas);

        modelo.getVista().tblConductoresR.setModel(model);
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoConductores))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 9) {
                    for (int i = 0; i < datos.length; i++) {
                        datos[i] = datos[i].trim();

                    }
                   model.addRow(new Object[]{
                datos[0], datos[1], datos[2], datos[3], datos[4],
                datos[5], datos[6], datos[7], datos[8], datos[9]
            });
                }

            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar la tabla: "+e.getMessage());
        }
        }
  public void exportarExcel() {
    String[] columnas = {
        "Código", "Nombre", "DPI", "Teléfono", "Dirección", 
        "Licencia", "Tipo Licencia", "Estado", "Fecha Ingreso", "Fecha Vencimiento"
    };
    logger.info("Iniciando exportación a excel");
    
    // Configurar fecha y ruta
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String rutaBase = "C:\\Users\\diego\\OneDrive\\Escritorio\\Reportes Transportes\\Reportes Conductores\\";
    String nombreArchivo = "Reporte Conductores (" + sdf.format(new Date()) + ").xlsx";
    
    try (BufferedReader reader = new BufferedReader(new FileReader(archivoConductores));
         XSSFWorkbook workbook = new XSSFWorkbook()) {
        
        // Crear directorio si no existe
        File directorio = new File(rutaBase);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        
        XSSFSheet sheet = workbook.createSheet("Conductores");
        
        // Crear encabezado
        Row header = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            header.createCell(i).setCellValue(columnas[i]);
        }
        
        // Procesar datos - versión mejorada
        String linea;
        int rowI = 1;
        int cuentaLinea = 0;
        
        while ((linea = reader.readLine()) != null) {
            cuentaLinea++;
            // Saltar líneas vacías
            if (linea.trim().isEmpty()) continue;
            
            // Versión más robusta del split
            String[] datos = linea.split("\\s*\\|\\s*", -1); // El -1 mantiene campos vacíos
            
            // Asegurar que tenemos suficientes columnas
            if (datos.length >= columnas.length) {
                Row row = sheet.createRow(rowI++);
                for (int i = 0; i < columnas.length; i++) {
                    // Manejar casos donde datos[i] podría no existir
                    String valor = (i < datos.length) ? datos[i].trim() : "";
                    row.createCell(i).setCellValue(valor);
                }
            } else {
                logger.warn("Línea " + cuentaLinea + " ignorada - Campos insuficientes: " + linea);
            }
        }
        
        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Guardar archivo
        File archivo = new File(directorio, nombreArchivo);
        try (FileOutputStream out = new FileOutputStream(archivo)) {
            workbook.write(out);
            String mensaje = "Reporte generado con " + (rowI-1) + " registros";
            modelo.getVista().lblRutaArchivo.setText(mensaje + " en: " + archivo.getAbsolutePath());
            JOptionPane.showMessageDialog(null, mensaje);
            logger.info(mensaje);
        }
        
    } catch (IOException e) {
        logger.error("Error al exportar excel", e);
        JOptionPane.showMessageDialog(null, "Error al exportar a Excel: " + e.getMessage(), 
        "Error", JOptionPane.ERROR_MESSAGE);
    }
}
        
        
        
    }