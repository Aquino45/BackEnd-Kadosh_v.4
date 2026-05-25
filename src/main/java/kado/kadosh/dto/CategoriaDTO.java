package kado.kadosh.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CategoriaDTO {
    private UUID categoriaId;
    private String nombre;
}