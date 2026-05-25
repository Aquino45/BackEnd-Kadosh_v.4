package kado.kadosh.dto;

import lombok.Data;

@Data
public class DetalleResponseDTO {
    private String productoNombre;
    private Integer cantidad;
    private Double precioVenta; // El precio que se guardó ese día
    private Double subtotal;
}