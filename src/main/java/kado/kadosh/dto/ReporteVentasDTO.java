package kado.kadosh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentasDTO {

    private UUID facturaId;
    private String numeroFactura;
    private LocalDateTime fechaEmision;

    private UUID usuarioId;
    private String clienteNombre;
    private String clienteDni;

    private Double subtotal;
    private Double igv;
    private Double total;

    private String estado;
}