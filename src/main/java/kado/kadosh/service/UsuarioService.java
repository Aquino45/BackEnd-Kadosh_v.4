package kado.kadosh.service;

import kado.kadosh.dto.ClienteListItemDTO;
import kado.kadosh.dto.PersonaUpdateDTO;
import kado.kadosh.dto.SubDatosUserDTO;
import kado.kadosh.entities.Persona;
import kado.kadosh.entities.Usuario;
import kado.kadosh.dto.ClienteSearchRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioService {
    Usuario save(Usuario usuario);
    Optional<Usuario> findByDni(String dni);
    Optional<Usuario> findByPersonaIdActivo(UUID personaId);
    Optional<Usuario> findById(UUID usuarioId);
    Optional<SubDatosUserDTO> subdatosPorUsuarioId(UUID usuarioId);
    List<Usuario> listar();
    Optional<String> rolUnicoPorUsuarioId(UUID usuarioId);

    List<ClienteListItemDTO> listarClientesOrdenados();

    Persona updatePersonaByUsuarioId(UUID usuarioId, PersonaUpdateDTO dto);

    void eliminarUsuario(UUID usuarioId);

    List<ClienteListItemDTO> buscarClientes(ClienteSearchRequest req);

    // 🚀 AÑADE ESTA LÍNEA PARA EL CAMBIO DE CONTRASEÑA
    void cambiarPassword(UUID usuarioId, String actualPassword, String newPassword);

    // 🚀 Añade esto a tu interfaz UsuarioService
    void resetearPasswordDirecto(UUID usuarioId, String newPassword);
}