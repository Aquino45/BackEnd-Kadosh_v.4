package kado.kadosh.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_optico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialOptico {

    @Id
    @Column(name = "historial_optico_id", columnDefinition = "RAW(16)")
    private UUID historialOpticoId;

    // ===========================
    // 🔗 Relaciones directas con Cascada
    // ===========================

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) // ✅ Cambiado a OneToOne + Cascada
    @OnDelete(action = OnDeleteAction.CASCADE) // 👈 Esto le dice a MySQL: "Aplica ON DELETE CASCADE"
    @JoinColumn(
            name = "vision_lejos_id",
            referencedColumnName = "vision_id",
            foreignKey = @ForeignKey(name = "fk_historial_vision_lejos")
    )

    private Vision visionLejos;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) // ✅ Cambiado a OneToOne + Cascada
    @OnDelete(action = OnDeleteAction.CASCADE) // 👈 Esto le dice a MySQL: "Aplica ON DELETE CASCADE"
    @JoinColumn(
            name = "vision_cerca_id",
            referencedColumnName = "vision_id",
            foreignKey = @ForeignKey(name = "fk_historial_vision_cerca")
    )
    private Vision visionCerca;


    // ===========================
    // 🔍 Interpretación de Resultados (4 Cuadrantes)
    // ===========================
    @Column(name = "analisis_lejos_od", length = 500)
    private String analisisLejosOD;

    @Column(name = "analisis_lejos_oi", length = 500)
    private String analisisLejosOI;

    @Column(name = "analisis_cerca_od", length = 500)
    private String analisisCercaOD;

    @Column(name = "analisis_cerca_oi", length = 500)
    private String analisisCercaOI;

    // ===========================
    // 🔗 Usuario propietario (De aquí jalaremos Nombre, DNI y Teléfono)
    // ===========================
    @ManyToOne
    @JoinColumn(
            name = "usuario_id",
            referencedColumnName = "usuario_id",
            foreignKey = @ForeignKey(name = "fk_historial_usuario")
    )
    private Usuario usuario;

    // ===========================
    // 📋 Datos del historial
    // ===========================
    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column
    private Integer edad; // Se mantiene porque es un dato histórico (edad al momento del examen)

    @Column(length = 1000)
    private String recomendaciones;

    @Column(length = 100)
    private String evaluador;

    // ===========================
    // 🖼️ Imágenes
    // ===========================
    @Column(length = 300)
    private String imagenRefraccionUrl;

    @Column(length = 300)
    private String imagenLenzometriaUrl;

    @Column(length = 300)
    private String imagenKeratometriaUrl;

    // ===========================
    // ⏱ Auditoría
    // ===========================
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}