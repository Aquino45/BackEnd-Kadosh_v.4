package kado.kadosh.dto;

import lombok.Data;

@Data
public class DetalleFacturaResponseDTO {
    private String productoNombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
