package kado.kadosh.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ItemRequestDTO {
    private UUID productoId;
    private Integer cantidad;
}