package kado.kadosh.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_consulta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialConsulta {

    @Id
    @Column(name = "historial_id", columnDefinition = "RAW(16)")
    private UUID historialId;

    @Column(name = "mensaje_usuario", nullable = false, length = 1000)
    private String mensajeUsuario;

    @Column(name = "respuesta_encontrada")
    private Boolean respuestaEncontrada;

    @Column(name = "cantidad_coincidencias")
    private Integer cantidadCoincidencias;

    @Column(name = "usuario_id", columnDefinition = "RAW(16)")
    private UUID usuarioId;

    @Column(name = "fecha_consulta")
    private LocalDateTime fechaConsulta;
}
