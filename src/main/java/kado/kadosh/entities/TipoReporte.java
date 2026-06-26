package kado.kadosh.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tipo_reporte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoReporte {

    @Id
    @Column(name = "tipo_reporte_id", columnDefinition = "RAW(16)")
    private UUID tipoReporteId;

    @Column(nullable = false, length = 50, unique = true)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "permite_pdf")
    private Boolean permitePdf = true;

    @Column(name = "permite_excel")
    private Boolean permiteExcel = true;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
