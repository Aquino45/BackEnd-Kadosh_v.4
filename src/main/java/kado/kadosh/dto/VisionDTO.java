package kado.kadosh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisionDTO {

    private BigDecimal ojoIzquierdoEsf;
    private BigDecimal ojoDerechoEsf;    // Esférico ojo derecho
    private BigDecimal ojoIzquierdoCil; // Cilíndrico ojo izquierdo
    private BigDecimal ojoDerechoCil;   // Cilíndrico ojo derecho
    private BigDecimal ojoIzquierdoEje; // Eje ojo izquierdo
    private BigDecimal ojoDerechoEje;   // Eje ojo derecho
    private BigDecimal ojoIzquierdoDip; // DIP ojo izquierdo
    private BigDecimal ojoDerechoDip;   // DIP ojo derecho
    private BigDecimal ojoIzquierdoAv;  // Agudeza visual ojo izquierdo
    private BigDecimal ojoDerechoAv;    // Agudeza visual ojo derecho
}
