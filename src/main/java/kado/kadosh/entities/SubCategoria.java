package kado.kadosh.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.UUID;

@Entity
@Table(name = "sub_categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoria {

    @Id
    @Column(name = "subcategoria_id", columnDefinition = "RAW(16)")
    private UUID subCategoriaId;

    @Column(length = 100, nullable = false)
    private String nombre; // Ej: "Carey"

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            name = "categoria_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_subcat_categoria")
    )
    private Categoria categoria; // Apunta a "Monturas"

    @Column(nullable = false)
    private Boolean activo = true;
}