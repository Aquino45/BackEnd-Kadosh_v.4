package kado.kadosh.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @Column(name = "producto_id", columnDefinition = "RAW(16)")
    private UUID productoId;

    @Column(length = 200, nullable = false)
    private String nombre; // Ej: "Aviador Retro X1"

    @Column(name = "precio_actual", nullable = false)
    private Double precioActual;

    @Column(nullable = false)
    private Integer stock;

    // FK 1: Categoría (Opcional si usas subcategoría, o viceversa)
    @ManyToOne
    @JoinColumn(
            name = "categoria_id",
            nullable = true,
            foreignKey = @ForeignKey(name = "fk_prod_categoria")
    )
    private Categoria categoria; //

    // FK 2: Subcategoría (Para el filtrado "Carey", "Metal", etc.)
    @ManyToOne
    @JoinColumn(
            name = "subcategoria_id",
            nullable = true,
            foreignKey = @ForeignKey(name = "fk_prod_subcategoria")
    )
    private SubCategoria subCategoria; //

    @Column(nullable = false)
    private Boolean activo = true;
}