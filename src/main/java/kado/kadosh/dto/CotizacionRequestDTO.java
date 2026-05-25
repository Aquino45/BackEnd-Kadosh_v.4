package kado.kadosh.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CotizacionRequestDTO {
    private UUID usuarioId;
    private UUID historialId;
    private String fechaCreacion; // 👈 TIENES QUE AGREGAR ESTO PARA RECIBIR LO QUE MANDA EL FRONT
    private List<ItemRequestDTO> items;
    private List<ItemServicioDTO> servicios;
}