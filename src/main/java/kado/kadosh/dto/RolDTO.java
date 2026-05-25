package kado.kadosh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {
    private UUID rolId;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private LocalDateTime createdAt;
}
