package kado.kadosh.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class SubCategoriaDTO {
    private UUID subCategoriaId;
    private String nombre;
    private UUID categoriaId; // Para saber a quién pertenece
}