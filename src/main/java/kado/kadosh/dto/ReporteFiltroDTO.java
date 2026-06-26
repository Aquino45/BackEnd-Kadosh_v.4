package kado.kadosh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteFiltroDTO {

    private String fechaInicio;
    private String fechaFin;

    private UUID usuarioId;

    private String estado;
    private Boolean estadoPago;

    private UUID categoriaId;
    private UUID subCategoriaId;
}