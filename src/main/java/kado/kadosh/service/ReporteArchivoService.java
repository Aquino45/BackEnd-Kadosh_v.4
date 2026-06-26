package kado.kadosh.service;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReporteArchivoService {

    byte[] exportarVentasExcel(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            String estado
    );

    byte[] exportarVentasPdf(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            String estado
    );

    byte[] exportarCotizacionesExcel(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            Boolean estadoPago
    );

    byte[] exportarCotizacionesPdf(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            Boolean estadoPago
    );

    byte[] exportarHistorialOpticoExcel(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId
    );

    byte[] exportarHistorialOpticoPdf(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId
    );

    byte[] exportarProductosVendidosExcel(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID categoriaId,
            UUID subCategoriaId
    );

    byte[] exportarProductosVendidosPdf(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID categoriaId,
            UUID subCategoriaId
    );
}