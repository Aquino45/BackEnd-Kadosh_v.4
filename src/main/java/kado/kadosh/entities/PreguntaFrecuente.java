package kado.kadosh.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pregunta_frecuente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaFrecuente {

    @Id
    @Column(name = "pregunta_id", columnDefinition = "RAW(16)")
    private UUID preguntaId;

    @Column(nullable = false, length = 500)
    private String pregunta;

    @Column(nullable = false, length = 2000)
    private String respuesta;

    @Column(name = "palabras_clave", length = 500)
    private String palabrasClave;

    @Column(length = 100)
    private String categoria;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
}
