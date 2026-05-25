package kado.kadosh.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import kado.kadosh.enums.EstadoUsuario; // 👈 Asegúrate de importar tu Enum
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @Column(name = "usuario_id", columnDefinition = "RAW(16)")
    private UUID usuarioId;

    // 1. Relación con Persona
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(
            name = "persona_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_usuario_persona")
    )
    private Persona persona;

    // 2. Relación con Apoderado
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(
            name = "apoderado_id",
            referencedColumnName = "apoderado_id",
            nullable = true
    )
    private Apoderado apoderado;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    // 🚨 CAMBIO AQUÍ: De Boolean a Enum
    // Antes: private Boolean activo = true;

    @Enumerated(EnumType.STRING) // 👈 Guarda el nombre "ACTIVO", "SEFUE", etc. en la BD
    @Column(name = "estado", nullable = false)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO; // Valor por defecto

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Set<UsuarioRol> usuarioRoles = new HashSet<>();

}