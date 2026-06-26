package kado.kadosh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResumenGeneralDTO {

    private Long totalFacturas;
    private Double totalVendido;

    private Long totalCotizaciones;
    private Long totalCotizacionesPagadas;
    private Long totalCotizacionesPendientes;

    private Long totalHistorialesOpticos;
    private Long totalProductosVendidos;
}