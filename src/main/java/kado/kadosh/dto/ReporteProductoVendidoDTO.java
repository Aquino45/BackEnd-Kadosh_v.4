package kado.kadosh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteProductoVendidoDTO {

    private UUID productoId;
    private String productoNombre;

    private String categoriaNombre;
    private String subCategoriaNombre;

    private Double precioActual;
    private Long cantidadVendida;
    private Double totalVendido;
}