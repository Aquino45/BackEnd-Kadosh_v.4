package kado.kadosh.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialOpticoDTO {

    private UUID historialOpticoId;
    private UUID usuarioId; // ID del dueño (Paciente)

    // ✅ Formato con barras y hora (Ej: 02/02/2026 13:50:49)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime fecha;

    // Estos datos ahora vendrán de la entidad Persona a través del Mapper
    private String dni;
    private String telefono;
    private String paciente; // Nombre completo
    private Integer edad;    // Dato histórico (del momento del examen)

    private VisionDTO visionLejos;
    private VisionDTO visionCerca;

    // ==========================================
    // 🔍 Interpretación de Resultados (Frontend res-text)
    // ==========================================
    private String analisisLejosOD;
    private String analisisLejosOI;
    private String analisisCercaOD;
    private String analisisCercaOI;

    private String imagenRefraccionUrl;
    private String imagenLenzometriaUrl;
    private String imagenKeratometriaUrl;

    private String recomendaciones;
    private String evaluador;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}