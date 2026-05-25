package kado.kadosh.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ProductoDTO {
    private UUID productoId;
    private String nombre;
    private Double precioActual;
    private Integer stock;
    private UUID categoriaId;
    private UUID subCategoriaId;
    private String nombreCategoria; // Para mostrarlo en tablas
    private String nombreSubCategoria;
}