package kado.kadosh.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoReporteDTO {

    private UUID tipoReporteId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Boolean permitePdf;
    private Boolean permiteExcel;
    private Boolean activo;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}