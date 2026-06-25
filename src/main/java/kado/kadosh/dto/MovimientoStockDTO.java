package kado.kadosh.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MovimientoStockDTO {
    private UUID movimientoId;
    private UUID productoId;
    private String nombreProducto;
    private String tipo;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private String motivo;
    private UUID usuarioId;
    private LocalDateTime fecha;
}
