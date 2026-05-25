package kado.kadosh.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @Column(name = "categoria_id", columnDefinition = "RAW(16)")
    private UUID categoriaId;

    @Column(length = 100, nullable = false)
    private String nombre; // Ej: "Monturas"

    @Column(nullable = false)
    private Boolean activo = true;
}