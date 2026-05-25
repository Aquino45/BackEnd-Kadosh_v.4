package kado.kadosh.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cotizacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cotizacion {

    @Id
    @Column(name = "cotizacion_id", columnDefinition = "RAW(16)")
    private UUID cotizacionId;

    @ManyToOne
    @JoinColumn(
            name = "usuario_id",
            referencedColumnName = "usuario_id",
            foreignKey = @ForeignKey(name = "fk_cotizacion_usuario")
    )
    @JsonIgnore
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(
            name = "historial_optico_id",
            referencedColumnName = "historial_optico_id",
            foreignKey = @ForeignKey(name = "fk_cotizacion_historial")
    )
    @JsonIgnore
    private HistorialOptico historialOptico;

    // 🔥 ESTE ES EL QUE NECESITAS PARA TU FRONT
    @Column(name = "fecha_creacion")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false)
    private Double total;

    @Column(name = "estado_pago", nullable = false)
    private Boolean estadoPago = false;

    // Esto es solo auditoría interna de la DB
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "fecha_pago")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime fechaPago;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<DetalleCotizacion> detalles;
}