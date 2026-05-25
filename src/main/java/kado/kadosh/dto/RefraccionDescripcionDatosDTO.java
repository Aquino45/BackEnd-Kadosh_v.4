package kado.kadosh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefraccionDescripcionDatosDTO {

    private UUID refraccionDescripcionDatosId;
    private String code;
    private String titulo;
    private String descripcion;
}
