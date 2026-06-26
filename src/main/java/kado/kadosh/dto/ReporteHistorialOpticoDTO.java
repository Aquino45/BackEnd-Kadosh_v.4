package kado.kadosh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteHistorialOpticoDTO {

    private UUID historialOpticoId;
    private LocalDateTime fecha;

    private UUID usuarioId;
    private String paciente;
    private String dni;
    private String telefono;

    private Integer edad;
    private String evaluador;
    private String recomendaciones;
}