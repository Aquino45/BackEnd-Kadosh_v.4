package kado.kadosh.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PreguntaFrecuenteDTO {
    private UUID preguntaId;
    private String pregunta;
    private String respuesta;
    private String palabrasClave;
    private String categoria;
    private Boolean activo;
    private LocalDateTime creadoEn;
}
