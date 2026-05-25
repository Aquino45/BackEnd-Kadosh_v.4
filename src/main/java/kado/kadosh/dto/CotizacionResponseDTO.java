package kado.kadosh.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CotizacionResponseDTO {
    private UUID cotizacionId;
    private String clienteNombre;
    private Double total;
    private Boolean estadoPago;

    // ✅ Cambiamos el nombre para que coincida con tu nueva Entidad
    private LocalDateTime createdAt;

    // ✅ Agregamos la edad para mostrarla en el historial de ventas
    private Integer edad;

    private LocalDateTime fechaCreacion; // 👈 CAMBIA EL NOMBRE AQUÍ TAMBIÉN

    private List<DetalleResponseDTO> detalles;
}