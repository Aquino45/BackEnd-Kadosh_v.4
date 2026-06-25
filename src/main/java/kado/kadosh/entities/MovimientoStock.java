package kado.kadosh.entities;

import jakarta.persistence.*;
import kado.kadosh.enums.TipoMovimientoStock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movimiento_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoStock {

    @Id
    @Column(name = "movimiento_id", columnDefinition = "RAW(16)")
    private UUID movimientoId;

    @ManyToOne
    @JoinColumn(
            name = "producto_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_mov_producto")
    )
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMovimientoStock tipo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;

    @Column(length = 300)
    private String motivo;

    @Column(name = "usuario_id", columnDefinition = "RAW(16)")
    private UUID usuarioId;

    @Column(name = "fecha")
    private LocalDateTime fecha;
}
