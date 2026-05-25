package kado.kadosh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRolDTO {
    private UUID usuarioRolId;
    private UUID usuarioId;
    private UUID rolId;
    private String nombreRol;
    private String nombreUsuario;


    public UsuarioRolDTO(UUID usuarioRolId, UUID usuarioId, UUID rolId, String nombreRol) {
        this.usuarioRolId = usuarioRolId;
        this.usuarioId = usuarioId;
        this.rolId = rolId;
        this.nombreRol = nombreRol;
    }
}
