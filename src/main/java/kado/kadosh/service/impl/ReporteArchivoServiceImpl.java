package kado.kadosh.service.impl;

import kado.kadosh.dto.ReporteCotizacionDTO;
import kado.kadosh.dto.ReporteHistorialOpticoDTO;
import kado.kadosh.dto.ReporteProductoVendidoDTO;
import kado.kadosh.dto.ReporteVentasDTO;
import kado.kadosh.service.ReporteArchivoService;
import kado.kadosh.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteArchivoServiceImpl implements ReporteArchivoService {

    private final ReporteService reporteService;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // =========================================================
    // VENTAS
    // =========================================================

    @Override
    public byte[] exportarVentasExcel(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            String estado
    ) {
        List<ReporteVentasDTO> data = reporteService.reporteVentas(
                fechaInicio,
                fechaFin,
                usuarioId,
                estado
        );

        String[] headers = {
                "N° Factura",
                "Fecha",
                "Cliente",
                "DNI",
                "Subtotal",
                "IGV",
                "Total",
                "Estado"
        };

        return generarExcel("Reporte de ventas", headers, data.stream()
                .map(r -> new String[]{
                        texto(r.getNumeroFactura()),
                        fecha(r.getFechaEmision()),
                        texto(r.getClienteNombre()),
                        texto(r.getClienteDni()),
                        numero(r.getSubtotal()),
                        numero(r.getIgv()),
                        numero(r.getTotal()),
                        texto(r.getEstado())
                })
                .toList()
        );
    }

    @Override
    public byte[] exportarVentasPdf(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            String estado
    ) {
        List<ReporteVentasDTO> data = reporteService.reporteVentas(
                fechaInicio,
                fechaFin,
                usuarioId,
                estado
        );

        String[] headers = {
                "N° Factura",
                "Fecha",
                "Cliente",
                "DNI",
                "Total",
                "Estado"
        };

        return generarPdf("Reporte de ventas", headers, data.stream()
                .map(r -> new String[]{
                        texto(r.getNumeroFactura()),
                        fecha(r.getFechaEmision()),
                        texto(r.getClienteNombre()),
                        texto(r.getClienteDni()),
                        numero(r.getTotal()),
                        texto(r.getEstado())
                })
                .toList()
        );
    }

    // =========================================================
    // COTIZACIONES
    // =========================================================

    @Override
    public byte[] exportarCotizacionesExcel(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            Boolean estadoPago
    ) {
        List<ReporteCotizacionDTO> data = reporteService.reporteCotizaciones(
                fechaInicio,
                fechaFin,
                usuarioId,
                estadoPago
        );

        String[] headers = {
                "Fecha",
                "Cliente",
                "DNI",
                "Edad",
                "Total",
                "Estado pago",
                "Fecha pago"
        };

        return generarExcel("Reporte de cotizaciones", headers, data.stream()
                .map(r -> new String[]{
                        fecha(r.getFechaCreacion()),
                        texto(r.getClienteNombre()),
                        texto(r.getClienteDni()),
                        texto(r.getEdad()),
                        numero(r.getTotal()),
                        Boolean.TRUE.equals(r.getEstadoPago()) ? "Pagado" : "Pendiente",
                        fecha(r.getFechaPago())
                })
                .toList()
        );
    }

    @Override
    public byte[] exportarCotizacionesPdf(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            Boolean estadoPago
    ) {
        List<ReporteCotizacionDTO> data = reporteService.reporteCotizaciones(
                fechaInicio,
                fechaFin,
                usuarioId,
                estadoPago
        );

        String[] headers = {
                "Fecha",
                "Cliente",
                "DNI",
                "Total",
                "Estado pago"
        };

        return generarPdf("Reporte de cotizaciones", headers, data.stream()
                .map(r -> new String[]{
                        fecha(r.getFechaCreacion()),
                        texto(r.getClienteNombre()),
                        texto(r.getClienteDni()),
                        numero(r.getTotal()),
                        Boolean.TRUE.equals(r.getEstadoPago()) ? "Pagado" : "Pendiente"
                })
                .toList()
        );
    }

    // =========================================================
    // HISTORIAL ÓPTICO
    // =========================================================

    @Override
    public byte[] exportarHistorialOpticoExcel(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId
    ) {
        List<ReporteHistorialOpticoDTO> data = reporteService.reporteHistorialOptico(
                fechaInicio,
                fechaFin,
                usuarioId
        );

        String[] headers = {
                "Fecha",
                "Paciente",
                "DNI",
                "Teléfono",
                "Edad",
                "Evaluador",
                "Recomendaciones"
        };

        return generarExcel("Reporte de historial óptico", headers, data.stream()
                .map(r -> new String[]{
                        fecha(r.getFecha()),
                        texto(r.getPaciente()),
                        texto(r.getDni()),
                        texto(r.getTelefono()),
                        texto(r.getEdad()),
                        texto(r.getEvaluador()),
                        texto(r.getRecomendaciones())
                })
                .toList()
        );
    }

    @Override
    public byte[] exportarHistorialOpticoPdf(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId
    ) {
        List<ReporteHistorialOpticoDTO> data = reporteService.reporteHistorialOptico(
                fechaInicio,
                fechaFin,
                usuarioId
        );

        String[] headers = {
                "Fecha",
                "Paciente",
                "DNI",
                "Edad",
                "Evaluador"
        };

        return generarPdf("Reporte de historial óptico", headers, data.stream()
                .map(r -> new String[]{
                        fecha(r.getFecha()),
                        texto(r.getPaciente()),
                        texto(r.getDni()),
                        texto(r.getEdad()),
                        texto(r.getEvaluador())
                })
                .toList()
        );
    }

    // =========================================================
    // PRODUCTOS VENDIDOS
    // =========================================================

    @Override
    public byte[] exportarProductosVendidosExcel(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID categoriaId,
            UUID subCategoriaId
    ) {
        List<ReporteProductoVendidoDTO> data = reporteService.reporteProductosVendidos(
                fechaInicio,
                fechaFin,
                categoriaId,
                subCategoriaId
        );

        String[] headers = {
                "Producto",
                "Categoría",
                "Subcategoría",
                "Precio actual",
                "Cantidad vendida",
                "Total vendido"
        };

        return generarExcel("Reporte de productos vendidos", headers, data.stream()
                .map(r -> new String[]{
                        texto(r.getProductoNombre()),
                        texto(r.getCategoriaNombre()),
                        texto(r.getSubCategoriaNombre()),
                        numero(r.getPrecioActual()),
                        texto(r.getCantidadVendida()),
                        numero(r.getTotalVendido())
                })
                .toList()
        );
    }

    @Override
    public byte[] exportarProductosVendidosPdf(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID categoriaId,
            UUID subCategoriaId
    ) {
        List<ReporteProductoVendidoDTO> data = reporteService.reporteProductosVendidos(
                fechaInicio,
                fechaFin,
                categoriaId,
                subCategoriaId
        );

        String[] headers = {
                "Producto",
                "Categoría",
                "Cantidad",
                "Total"
        };

        return generarPdf("Reporte de productos vendidos", headers, data.stream()
                .map(r -> new String[]{
                        texto(r.getProductoNombre()),
                        texto(r.getCategoriaNombre()),
                        texto(r.getCantidadVendida()),
                        numero(r.getTotalVendido())
                })
                .toList()
        );
    }

    // =========================================================
    // GENERADORES
    // =========================================================

    private byte[] generarExcel(String titulo, String[] headers, List<String[]> filas) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(titulo);

            Row tituloRow = sheet.createRow(0);
            tituloRow.createCell(0).setCellValue(titulo);

            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIndex = 3;

            for (String[] fila : filas) {
                Row row = sheet.createRow(rowIndex++);

                for (int i = 0; i < fila.length; i++) {
                    row.createCell(i).setCellValue(fila[i]);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel: " + e.getMessage(), e);
        }
    }

    private byte[] generarPdf(String titulo, String[] headers, List<String[]> filas) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();

            Paragraph title = new Paragraph(
                    titulo,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
            );

            document.add(title);
            document.add(new Paragraph("Generado: " + fecha(LocalDateTime.now())));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);

            for (String header : headers) {
                table.addCell(header);
            }

            for (String[] fila : filas) {
                for (String celda : fila) {
                    table.addCell(celda);
                }
            }

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private String texto(Object valor) {
        return valor == null ? "" : String.valueOf(valor);
    }

    private String numero(Double valor) {
        return valor == null ? "0.00" : String.format("%.2f", valor);
    }

    private String fecha(LocalDateTime fecha) {
        return fecha == null ? "" : fecha.format(FORMATO_FECHA);
    }
}