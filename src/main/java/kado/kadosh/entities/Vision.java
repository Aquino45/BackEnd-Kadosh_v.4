package kado.kadosh.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Vision")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vision {

    @Id
    @Column(name = "vision_id", columnDefinition = "RAW(16)")
    private UUID visionId;

    @Column(name = "tipo_vision", length = 10)
    private String tipoVision;

    @Column(name = "ojo_izquierdo_esf", precision = 5, scale = 2)
    private BigDecimal ojoIzquierdoEsf;

    @Column(name = "ojo_derecho_esf", precision = 5, scale = 2)
    private BigDecimal ojoDerechoEsf;

    @Column(name = "ojo_izquierdo_cil", precision = 5, scale = 2)
    private BigDecimal ojoIzquierdoCil;

    @Column(name = "ojo_derecho_cil", precision = 5, scale = 2)
    private BigDecimal ojoDerechoCil;

    @Column(name = "ojo_izquierdo_eje", precision = 5, scale = 2)
    private BigDecimal ojoIzquierdoEje;

    @Column(name = "ojo_derecho_eje", precision = 5, scale = 2)
    private BigDecimal ojoDerechoEje;

    @Column(name = "ojo_izquierdo_dip", precision = 5, scale = 2)
    private BigDecimal ojoIzquierdoDip;

    @Column(name = "ojo_derecho_dip", precision = 5, scale = 2)
    private BigDecimal ojoDerechoDip;

    @Column(name = "ojo_izquierdo_av", precision = 5, scale = 2)
    private BigDecimal ojoIzquierdoAv;

    @Column(name = "ojo_derecho_av", precision = 5, scale = 2)
    private BigDecimal ojoDerechoAv;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
