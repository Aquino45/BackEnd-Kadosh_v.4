package kado.kadosh.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "refraccion_descripcion_datos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefraccionDescripcionDatos {

    @Id
    @Column(name = "refraccion_descripcion_datos_id", columnDefinition = "RAW(16)")
    private UUID refraccionDescripcionDatosId;

    // ESF, CIL, EJE, DIP, AV
    @Column(length = 20, nullable = false)
    private String code;

    @Column(length = 100, nullable = false)
    private String titulo;

    @Column(length = 600, nullable = false)
    private String descripcion;
}
