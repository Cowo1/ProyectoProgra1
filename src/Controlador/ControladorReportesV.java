/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author diego
 */
import Modelo.ModeloReportesV;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControladorReportesV implements ActionListener {

    private ModeloReportesV modelo;
    private File archivoVentas = new File("ventas.txt");

    public ControladorReportesV(ModeloReportesV modelo) {
        this.modelo = modelo;
        
        cargarTabla();
    }
    // Maneja el evento del botón "Generar", que exporta el reporte de conductores a un archivo Excel

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modelo.getVista().btnGenerar) {
            exportarExcel();
        }
    }
// Carga los datos de las ventas desde el archivo y las muestra en la tabla de la vista

    public void cargarTabla() {
        DefaultTableModel model = (DefaultTableModel) modelo.getVista().tblVentasR.getModel();
        model.setRowCount(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoVentas))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length == 10) {
                    Object[] fila = new Object[10];
                    for (int i = 0; i < 10; i++) {
                        fila[i] = datos[i].trim();
                    }
                    model.addRow(fila);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos de ventas.");
        }
    }
// Exporta los datos de las ventas a un archivo Excel con formato .xlsx

    public void exportarExcel() {
        String[] columnas = {
            "Código Ruta", "Nombre Ruta", "Destino", "Horario", "Precio", 
            "Placa Bus", "Fecha Viaje", "Nombre Pasajero", "DPI", "Teléfono"
        };

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Ventas");
        fileChooser.setSelectedFile(new File("Reporte Ventas (" + sdf.format(new Date()) + ").xlsx"));

        int seleccion = fileChooser.showSaveDialog(null);
        if (seleccion != JFileChooser.APPROVE_OPTION) return;

        File archivo = fileChooser.getSelectedFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoVentas));
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet sheet = workbook.createSheet("Ventas");
            Row header = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            String linea;
            int rowI = 1;

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] datos = linea.split("\\s*\\|\\s*", -1);
                if (datos.length == 10) {
                    Row row = sheet.createRow(rowI++);
                    for (int i = 0; i < 10; i++) {
                        row.createCell(i).setCellValue(datos[i].trim());
                    }
                }
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(archivo)) {
                workbook.write(out);
                String mensaje = "Reporte generado con " + (rowI - 1) + " registros";
                modelo.getVista().lblRutaArchivo.setText(mensaje + " en: " + archivo.getAbsolutePath());
                JOptionPane.showMessageDialog(null, mensaje);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al exportar a Excel: " + e.getMessage());
        }
    }
}
