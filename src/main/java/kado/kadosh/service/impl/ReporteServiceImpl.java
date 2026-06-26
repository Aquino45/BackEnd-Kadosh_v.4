package kado.kadosh.service.impl;

import kado.kadosh.dto.ReporteCotizacionDTO;
import kado.kadosh.dto.ReporteHistorialOpticoDTO;
import kado.kadosh.dto.ReporteProductoVendidoDTO;
import kado.kadosh.dto.ReporteResumenGeneralDTO;
import kado.kadosh.dto.ReporteVentasDTO;
import kado.kadosh.repository.CotizacionRepository;
import kado.kadosh.repository.DetalleFacturaRepository;
import kado.kadosh.repository.FacturaRepository;
import kado.kadosh.repository.HistorialOpticoRepository;
import kado.kadosh.repository.UsuarioRepository;
import kado.kadosh.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private final FacturaRepository facturaRepository;
    private final CotizacionRepository cotizacionRepository;
    private final HistorialOpticoRepository historialOpticoRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<ReporteVentasDTO> reporteVentas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            String estado
    ) {
        return facturaRepository.reporteVentas(
                fechaInicio,
                fechaFin,
                usuarioId,
                normalizarTexto(estado)
        );
    }

    @Override
    public List<ReporteCotizacionDTO> reporteCotizaciones(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId,
            Boolean estadoPago
    ) {
        return cotizacionRepository.reporteCotizaciones(
                fechaInicio,
                fechaFin,
                usuarioId,
                estadoPago
        );
    }

    @Override
    public List<ReporteHistorialOpticoDTO> reporteHistorialOptico(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID usuarioId
    ) {
        List<ReporteHistorialOpticoDTO> data = historialOpticoRepository.reporteHistorialOptico(
                fechaInicio,
                fechaFin,
                usuarioId
        );

        data.forEach(dto -> dto.setEvaluador(resolverNombreEvaluador(dto.getEvaluador())));

        return data;
    }

    @Override
    public List<ReporteProductoVendidoDTO> reporteProductosVendidos(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            UUID categoriaId,
            UUID subCategoriaId
    ) {
        return detalleFacturaRepository.reporteProductosVendidos(
                fechaInicio,
                fechaFin,
                categoriaId,
                subCategoriaId
        );
    }

    @Override
    public ReporteResumenGeneralDTO resumenGeneral(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) {
        Long totalFacturas = facturaRepository.contarFacturasReporte(fechaInicio, fechaFin);
        Double totalVendido = facturaRepository.totalVendidoReporte(fechaInicio, fechaFin);

        Long totalCotizaciones = cotizacionRepository.contarCotizacionesReporte(fechaInicio, fechaFin);
        Long totalCotizacionesPagadas = cotizacionRepository.contarCotizacionesPagadasReporte(fechaInicio, fechaFin);
        Long totalCotizacionesPendientes = cotizacionRepository.contarCotizacionesPendientesReporte(fechaInicio, fechaFin);

        Long totalHistorialesOpticos = historialOpticoRepository.contarHistorialesReporte(fechaInicio, fechaFin);
        Long totalProductosVendidos = detalleFacturaRepository.contarProductosVendidosReporte(fechaInicio, fechaFin);

        return new ReporteResumenGeneralDTO(
                totalFacturas,
                totalVendido,
                totalCotizaciones,
                totalCotizacionesPagadas,
                totalCotizacionesPendientes,
                totalHistorialesOpticos,
                totalProductosVendidos
        );
    }

    private String normalizarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }

    private String resolverNombreEvaluador(String evaluadorRaw) {
        if (evaluadorRaw == null || evaluadorRaw.isBlank()) {
            return "Sin evaluador";
        }

        try {
            UUID evaluadorId = UUID.fromString(evaluadorRaw);

            return usuarioRepository.findById(evaluadorId)
                    .map(usuario -> {
                        if (usuario.getPersona() == null) {
                            return "Evaluador sin datos personales";
                        }

                        String nombre = usuario.getPersona().getNombre() != null
                                ? usuario.getPersona().getNombre()
                                : "";

                        String apellido = usuario.getPersona().getApellido() != null
                                ? usuario.getPersona().getApellido()
                                : "";

                        String nombreCompleto = (nombre + " " + apellido).trim();

                        return nombreCompleto.isBlank()
                                ? "Evaluador sin nombre"
                                : nombreCompleto;
                    })
                    .orElse("Usuario evaluador no encontrado");

        } catch (IllegalArgumentException e) {
            return evaluadorRaw;
        }
    }
}