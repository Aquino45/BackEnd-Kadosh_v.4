package kado.kadosh.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Apoderado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Apoderado {

    @Id
// ❌ BORRA ESTO: @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "apoderado_id", columnDefinition = "RAW(16)")
    private UUID apoderadoId;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 100, nullable = false)
    private String apellido;

    @Column(length = 8, nullable = false)
    private String dni;

    @Column(length = 20, nullable = false)
    private String telefono;

    @Column(length = 50) // Ejemplo: Padre, Madre, Hijo, Tutor
    private String parentesco;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}