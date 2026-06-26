package kado.kadosh.service;

import kado.kadosh.dto.ReporteCotizacionDTO;
import kado.kadosh.dto.ReporteHistorialOpticoDTO;
import kado.kadosh.dto.ReporteProductoVendidoDTO;
import kado.kadosh.dto.ReporteResumenGeneralDTO;
import kado.kadosh.dto.ReporteVentasDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReporteService {

    List<ReporteVentasDTO> reporteVentas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            String estado
    );

    List<ReporteCotizacionDTO> reporteCotizaciones(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            Boolean estadoPago
    );

    List<ReporteHistorialOpticoDTO> reporteHistorialOptico(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId
    );

    List<ReporteProductoVendidoDTO> reporteProductosVendidos(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID categoriaId,
            UUID subCategoriaId
    );

    ReporteResumenGeneralDTO resumenGeneral(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}