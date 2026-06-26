package kado.kadosh.controller;

import kado.kadosh.dto.ReporteFiltroDTO;
import kado.kadosh.service.ReporteArchivoService;
import kado.kadosh.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('Admin','Cajero','Optometrista')")
public class ReporteController {

    private final ReporteService reporteService;
    private final ReporteArchivoService reporteArchivoService;

    private static final DateTimeFormatter FORMATO_FECHA_SIMPLE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter FORMATO_FECHA_HORA_SIMPLE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter FORMATO_FECHA_PERU =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_FECHA_HORA_PERU =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // =========================================================
    // REPORTES EN VIVO - GET CON PARAMS
    // =========================================================

    @GetMapping("/ventas")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<?> reporteVentas(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) String estado
    ) {
        var data = reporteService.reporteVentas(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                usuarioId,
                estado
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    @GetMapping("/cotizaciones")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<?> reporteCotizaciones(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) Boolean estadoPago
    ) {
        var data = reporteService.reporteCotizaciones(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                usuarioId,
                estadoPago
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    @GetMapping("/historial-optico")
    @PreAuthorize("hasAnyRole('Admin','Cajero','Optometrista')")
    public ResponseEntity<?> reporteHistorialOptico(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID usuarioId
    ) {
        var data = reporteService.reporteHistorialOptico(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                usuarioId
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    @GetMapping("/productos-vendidos")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<?> reporteProductosVendidos(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) UUID subCategoriaId
    ) {
        var data = reporteService.reporteProductosVendidos(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                categoriaId,
                subCategoriaId
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    @GetMapping("/resumen-general")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<?> resumenGeneral(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin
    ) {
        var data = reporteService.resumenGeneral(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin)
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    // =========================================================
    // REPORTES EN VIVO - POST CON JSON
    // =========================================================

    @PostMapping("/ventas")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<?> reporteVentasJson(@RequestBody ReporteFiltroDTO filtro) {
        var data = reporteService.reporteVentas(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getUsuarioId(),
                filtro.getEstado()
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    @PostMapping("/cotizaciones")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<?> reporteCotizacionesJson(@RequestBody ReporteFiltroDTO filtro) {
        var data = reporteService.reporteCotizaciones(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getUsuarioId(),
                filtro.getEstadoPago()
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    @PostMapping("/historial-optico")
    @PreAuthorize("hasAnyRole('Admin','Cajero','Optometrista')")
    public ResponseEntity<?> reporteHistorialOpticoJson(@RequestBody ReporteFiltroDTO filtro) {
        var data = reporteService.reporteHistorialOptico(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getUsuarioId()
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    @PostMapping("/productos-vendidos")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<?> reporteProductosVendidosJson(@RequestBody ReporteFiltroDTO filtro) {
        var data = reporteService.reporteProductosVendidos(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getCategoriaId(),
                filtro.getSubCategoriaId()
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    @PostMapping("/resumen-general")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<?> resumenGeneralJson(@RequestBody ReporteFiltroDTO filtro) {
        var data = reporteService.resumenGeneral(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin())
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    // =========================================================
    // EXPORTAR VENTAS - GET CON PARAMS
    // =========================================================

    @GetMapping("/ventas/exportar/pdf")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarVentasPdf(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) String estado
    ) {
        byte[] archivo = reporteArchivoService.exportarVentasPdf(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                usuarioId,
                estado
        );

        return descargarArchivo(
                archivo,
                "reporte_ventas.pdf",
                MediaType.APPLICATION_PDF_VALUE
        );
    }

    @GetMapping("/ventas/exportar/excel")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarVentasExcel(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) String estado
    ) {
        byte[] archivo = reporteArchivoService.exportarVentasExcel(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                usuarioId,
                estado
        );

        return descargarArchivo(
                archivo,
                "reporte_ventas.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }

    // =========================================================
    // EXPORTAR COTIZACIONES - GET CON PARAMS
    // =========================================================

    @GetMapping("/cotizaciones/exportar/pdf")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarCotizacionesPdf(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) Boolean estadoPago
    ) {
        byte[] archivo = reporteArchivoService.exportarCotizacionesPdf(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                usuarioId,
                estadoPago
        );

        return descargarArchivo(
                archivo,
                "reporte_cotizaciones.pdf",
                MediaType.APPLICATION_PDF_VALUE
        );
    }

    @GetMapping("/cotizaciones/exportar/excel")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarCotizacionesExcel(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) Boolean estadoPago
    ) {
        byte[] archivo = reporteArchivoService.exportarCotizacionesExcel(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                usuarioId,
                estadoPago
        );

        return descargarArchivo(
                archivo,
                "reporte_cotizaciones.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }

    // =========================================================
    // EXPORTAR HISTORIAL ÓPTICO - GET CON PARAMS
    // =========================================================

    @GetMapping("/historial-optico/exportar/pdf")
    @PreAuthorize("hasAnyRole('Admin','Cajero','Optometrista')")
    public ResponseEntity<byte[]> exportarHistorialOpticoPdf(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID usuarioId
    ) {
        byte[] archivo = reporteArchivoService.exportarHistorialOpticoPdf(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                usuarioId
        );

        return descargarArchivo(
                archivo,
                "reporte_historial_optico.pdf",
                MediaType.APPLICATION_PDF_VALUE
        );
    }

    @GetMapping("/historial-optico/exportar/excel")
    @PreAuthorize("hasAnyRole('Admin','Cajero','Optometrista')")
    public ResponseEntity<byte[]> exportarHistorialOpticoExcel(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID usuarioId
    ) {
        byte[] archivo = reporteArchivoService.exportarHistorialOpticoExcel(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                usuarioId
        );

        return descargarArchivo(
                archivo,
                "reporte_historial_optico.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }

    // =========================================================
    // EXPORTAR PRODUCTOS VENDIDOS - GET CON PARAMS
    // =========================================================

    @GetMapping("/productos-vendidos/exportar/pdf")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarProductosVendidosPdf(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) UUID subCategoriaId
    ) {
        byte[] archivo = reporteArchivoService.exportarProductosVendidosPdf(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                categoriaId,
                subCategoriaId
        );

        return descargarArchivo(
                archivo,
                "reporte_productos_vendidos.pdf",
                MediaType.APPLICATION_PDF_VALUE
        );
    }

    @GetMapping("/productos-vendidos/exportar/excel")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarProductosVendidosExcel(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) UUID subCategoriaId
    ) {
        byte[] archivo = reporteArchivoService.exportarProductosVendidosExcel(
                parseFechaInicio(fechaInicio),
                parseFechaFin(fechaFin),
                categoriaId,
                subCategoriaId
        );

        return descargarArchivo(
                archivo,
                "reporte_productos_vendidos.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }

    // =========================================================
    // EXPORTAR PDF Y EXCEL - POST CON JSON
    // =========================================================

    @PostMapping("/ventas/exportar/pdf")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarVentasPdfJson(@RequestBody ReporteFiltroDTO filtro) {
        byte[] archivo = reporteArchivoService.exportarVentasPdf(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getUsuarioId(),
                filtro.getEstado()
        );

        return descargarArchivo(
                archivo,
                "reporte_ventas.pdf",
                MediaType.APPLICATION_PDF_VALUE
        );
    }

    @PostMapping("/ventas/exportar/excel")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarVentasExcelJson(@RequestBody ReporteFiltroDTO filtro) {
        byte[] archivo = reporteArchivoService.exportarVentasExcel(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getUsuarioId(),
                filtro.getEstado()
        );

        return descargarArchivo(
                archivo,
                "reporte_ventas.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }

    @PostMapping("/cotizaciones/exportar/pdf")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarCotizacionesPdfJson(@RequestBody ReporteFiltroDTO filtro) {
        byte[] archivo = reporteArchivoService.exportarCotizacionesPdf(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getUsuarioId(),
                filtro.getEstadoPago()
        );

        return descargarArchivo(
                archivo,
                "reporte_cotizaciones.pdf",
                MediaType.APPLICATION_PDF_VALUE
        );
    }

    @PostMapping("/cotizaciones/exportar/excel")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarCotizacionesExcelJson(@RequestBody ReporteFiltroDTO filtro) {
        byte[] archivo = reporteArchivoService.exportarCotizacionesExcel(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getUsuarioId(),
                filtro.getEstadoPago()
        );

        return descargarArchivo(
                archivo,
                "reporte_cotizaciones.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }

    @PostMapping("/historial-optico/exportar/pdf")
    @PreAuthorize("hasAnyRole('Admin','Cajero','Optometrista')")
    public ResponseEntity<byte[]> exportarHistorialOpticoPdfJson(@RequestBody ReporteFiltroDTO filtro) {
        byte[] archivo = reporteArchivoService.exportarHistorialOpticoPdf(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getUsuarioId()
        );

        return descargarArchivo(
                archivo,
                "reporte_historial_optico.pdf",
                MediaType.APPLICATION_PDF_VALUE
        );
    }

    @PostMapping("/historial-optico/exportar/excel")
    @PreAuthorize("hasAnyRole('Admin','Cajero','Optometrista')")
    public ResponseEntity<byte[]> exportarHistorialOpticoExcelJson(@RequestBody ReporteFiltroDTO filtro) {
        byte[] archivo = reporteArchivoService.exportarHistorialOpticoExcel(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getUsuarioId()
        );

        return descargarArchivo(
                archivo,
                "reporte_historial_optico.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }

    @PostMapping("/productos-vendidos/exportar/pdf")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarProductosVendidosPdfJson(@RequestBody ReporteFiltroDTO filtro) {
        byte[] archivo = reporteArchivoService.exportarProductosVendidosPdf(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getCategoriaId(),
                filtro.getSubCategoriaId()
        );

        return descargarArchivo(
                archivo,
                "reporte_productos_vendidos.pdf",
                MediaType.APPLICATION_PDF_VALUE
        );
    }

    @PostMapping("/productos-vendidos/exportar/excel")
    @PreAuthorize("hasAnyRole('Admin','Cajero')")
    public ResponseEntity<byte[]> exportarProductosVendidosExcelJson(@RequestBody ReporteFiltroDTO filtro) {
        byte[] archivo = reporteArchivoService.exportarProductosVendidosExcel(
                parseFechaInicio(filtro.getFechaInicio()),
                parseFechaFin(filtro.getFechaFin()),
                filtro.getCategoriaId(),
                filtro.getSubCategoriaId()
        );

        return descargarArchivo(
                archivo,
                "reporte_productos_vendidos.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }

    // =========================================================
    // DESCARGA DE ARCHIVO
    // =========================================================

    private ResponseEntity<byte[]> descargarArchivo(
            byte[] archivo,
            String nombreArchivo,
            String contentType
    ) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(archivo);
    }

    // =========================================================
    // PARSE FECHAS
    // =========================================================

    private LocalDateTime parseFechaInicio(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return null;
        }

        fecha = fecha.trim();

        try {
            if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(fecha, FORMATO_FECHA_SIMPLE).atStartOfDay();
            }

            if (fecha.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                return LocalDateTime.parse(fecha, FORMATO_FECHA_HORA_SIMPLE);
            }

            if (fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return LocalDate.parse(fecha, FORMATO_FECHA_PERU).atStartOfDay();
            }

            if (fecha.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}")) {
                return LocalDateTime.parse(fecha, FORMATO_FECHA_HORA_PERU);
            }

            throw new RuntimeException("Formato de fecha inválido: " + fecha);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo convertir la fecha de inicio: " + fecha);
        }
    }

    private LocalDateTime parseFechaFin(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return null;
        }

        fecha = fecha.trim();

        try {
            if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(fecha, FORMATO_FECHA_SIMPLE).atTime(23, 59, 59);
            }

            if (fecha.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                return LocalDateTime.parse(fecha, FORMATO_FECHA_HORA_SIMPLE);
            }

            if (fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return LocalDate.parse(fecha, FORMATO_FECHA_PERU).atTime(23, 59, 59);
            }

            if (fecha.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}")) {
                return LocalDateTime.parse(fecha, FORMATO_FECHA_HORA_PERU);
            }

            throw new RuntimeException("Formato de fecha inválido: " + fecha);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo convertir la fecha de fin: " + fecha);
        }
    }
}
