
package kado.kadosh.service;

import kado.kadosh.dto.UsuarioRolDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRolService {
    UsuarioRolDTO asignarRol(UUID usuarioId, UUID rolId);
    boolean quitarRol(UUID usuarioId, UUID rolId);
    List<UsuarioRolDTO> listarRolesDeUsuario(UUID usuarioId);
    Optional<String> rolNombrePorUsuarioId(UUID usuarioId);
    UsuarioRolDTO asignarRolPorNombre(UUID usuarioId, String nombreRol);
    
}
