package kado.kadosh.dto;

import lombok.Data;

@Data
public class AjusteStockRequestDTO {
    private Integer cantidad;
    private String tipo; // ENTRADA, SALIDA o AJUSTE
    private String motivo;
}
