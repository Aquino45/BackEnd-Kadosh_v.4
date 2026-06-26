package kado.kadosh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCotizacionDTO {

    private UUID cotizacionId;
    private LocalDateTime fechaCreacion;

    private UUID usuarioId;
    private String clienteNombre;
    private String clienteDni;

    private Integer edad;
    private Double total;
    private Boolean estadoPago;
    private LocalDateTime fechaPago;
}