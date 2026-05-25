package kado.kadosh.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class FacturaResponseDTO {
    private UUID facturaId;
    private UUID cotizacionId;
    private String numero;
    private String clienteNombre;
    private String clienteDni;
    private LocalDateTime fechaEmision;
    private Double subtotal;
    private Double igv;
    private Double total;
    private String estado;
    private List<DetalleFacturaResponseDTO> detalles;
}
