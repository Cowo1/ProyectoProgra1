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
    DefaultTableModel model = (DefaultTableModel) modelo.getVista().tblConductoresR.getModel();
    model.setRowCount(0);

    try (BufferedReader reader = new BufferedReader(new FileReader("conductores.txt"))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 10) {
                String codigo = datos[0].trim();
                String nombre = datos[1].trim();
                String telefono = datos[3].trim();
                String licencia = datos[5].trim();
                String tipo = datos[6].trim();
                String vencimiento = datos[8].trim();
                String estado = datos[7].trim();
                String fechaIngreso = datos[9].trim();

                String placaBus = obtenerPlacaBusAsignado(codigo);

                model.addRow(new Object[]{
                    codigo, nombre, telefono, licencia, tipo, vencimiento, estado, fechaIngreso, placaBus
                });
            }
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(null, "Error al cargar los datos de los conductores.");
    }
}

  public void exportarExcel() {
    String[] columnas = { "Código", "Nombre", "Teléfono", "Licencia", "Tipo", "Fecha Ingreso", "Estado", "Vencimiento", "Placa Bus" };

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String rutaBase = "C:\\Users\\diego\\OneDrive\\Escritorio\\Reportes Transportes\\Reportes Conductores\\";
    String nombreArchivo = "Reporte Conductores (" + sdf.format(new Date()) + ").xlsx";

    
    try (BufferedReader reader = new BufferedReader(new FileReader("conductores.txt"));
         XSSFWorkbook workbook = new XSSFWorkbook()) {

        File directorio = new File(rutaBase);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        XSSFSheet sheet = workbook.createSheet("Conductores");
        Row header = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            header.createCell(i).setCellValue(columnas[i]);
        }

        String linea;
        int rowI = 1;
        int cuentaLinea = 0;

        while ((linea = reader.readLine()) != null) {
            cuentaLinea++;
            if (linea.trim().isEmpty()) continue;

            String[] datos = linea.split("\\s*\\|\\s*", -1);

            if (datos.length >= 10) {
                String codigo = datos[0].trim();
                String nombre = datos[1].trim();
                String telefono = datos[3].trim();
                String licencia = datos[5].trim();
                String tipo = datos[6].trim();
                String vencimiento = datos[8].trim();
                String estado = datos[7].trim();
                String fechaIngreso = datos[9].trim();

                // Buscar la placa del bus asignado
                String placaBus = obtenerPlacaBusAsignado(codigo);

                Row row = sheet.createRow(rowI++);
                row.createCell(0).setCellValue(codigo);
                row.createCell(1).setCellValue(nombre);
                row.createCell(2).setCellValue(telefono);
                row.createCell(3).setCellValue(licencia);
                row.createCell(4).setCellValue(tipo);
                row.createCell(5).setCellValue(vencimiento);
                row.createCell(6).setCellValue(estado);
                row.createCell(7).setCellValue(fechaIngreso);
                row.createCell(8).setCellValue(placaBus);
            } else {
                System.out.println("Línea ignorada: " + linea);
            }
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        File archivo = new File(directorio, nombreArchivo);
        try (FileOutputStream out = new FileOutputStream(archivo)) {
            workbook.write(out);
            String mensaje = "Reporte generado con " + (rowI - 1) + " registros";
            modelo.getVista().lblRutaArchivo.setText(mensaje + " en: " + archivo.getAbsolutePath());
            JOptionPane.showMessageDialog(null, mensaje);
        }

    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al exportar a Excel: " + e.getMessage());
    }
}

        public String obtenerPlacaBusAsignado(String codigoConductor) {
    try (BufferedReader reader = new BufferedReader(new FileReader("asignaciones.txt"))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 3 && datos[0].trim().equals(codigoConductor)) {
                return datos[2].trim(); // Placa del bus
            }
        }
    } catch (IOException e) {
        // puedes mostrar un mensaje o manejar el error
    }
    return "-----";
        }
        
    }