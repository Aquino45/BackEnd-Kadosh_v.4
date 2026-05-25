package kado.kadosh.service.impl;

import kado.kadosh.dto.UsuarioRolDTO;
import kado.kadosh.entities.Rol;
import kado.kadosh.entities.Usuario;
import kado.kadosh.entities.UsuarioRol;
import kado.kadosh.repository.RolRepository;
import kado.kadosh.repository.UsuarioRepository;
import kado.kadosh.repository.UsuarioRolRepository;
import kado.kadosh.service.UsuarioRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioRolServiceImpl implements UsuarioRolService {

    private final UsuarioRolRepository usuarioRolRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Override
    public UsuarioRolDTO asignarRol(UUID usuarioId, UUID rolId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));

        boolean yaTiene = usuarioRolRepository.existsByUsuario_UsuarioIdAndRol_RolId(usuarioId, rolId);
        if (yaTiene) {
            UsuarioRol existente = usuarioRolRepository
                    .findByUsuario_UsuarioIdAndRol_RolId(usuarioId, rolId)
                    .orElseThrow();

            UsuarioRolDTO dto = new UsuarioRolDTO(
                    existente.getUsuarioRolId(),
                    usuarioId,
                    rolId,
                    existente.getRol().getNombre()
            );
            if (usuario.getPersona() != null) {
                dto.setNombreUsuario(usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido());
            }
            return dto;
        }

        UsuarioRol ur = new UsuarioRol();
        ur.setUsuarioRolId(UUID.randomUUID());
        ur.setUsuario(usuario);
        ur.setRol(rol);
        ur.setActivo(true);
        usuarioRolRepository.save(ur);

        UsuarioRolDTO dto = new UsuarioRolDTO(
                ur.getUsuarioRolId(),
                usuarioId,
                rolId,
                rol.getNombre()
        );
        if (usuario.getPersona() != null) {
            dto.setNombreUsuario(usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido());
        }
        return dto;
    }

    @Override
    public boolean quitarRol(UUID usuarioId, UUID rolId) {
        Integer eliminados = usuarioRolRepository.deleteByUsuario_UsuarioIdAndRol_RolId(usuarioId, rolId);
        return eliminados != null && eliminados > 0;
    }


    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRolDTO> listarRolesDeUsuario(UUID usuarioId) {
        return usuarioRolRepository.findByUsuario_UsuarioId(usuarioId)
                .stream()
                .filter(ur -> ur.getRol() != null)
                .map(ur -> new UsuarioRolDTO(
                        ur.getUsuarioRolId(),
                        usuarioId,
                        ur.getRol().getRolId(),
                        ur.getRol().getNombre()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> rolNombrePorUsuarioId(UUID usuarioId) {
        return usuarioRolRepository
                .findFirstByUsuario_UsuarioIdOrderByCreatedAtDesc(usuarioId)
                .map(UsuarioRol::getRol)
                .map(r -> r != null ? r.getNombre() : null);
    }

    @Override
    public UsuarioRolDTO asignarRolPorNombre(UUID usuarioId, String nombreRol) {
        String buscado = nombreRol == null ? "" : nombreRol.trim();
        if (buscado.isEmpty()) throw new IllegalArgumentException("Nombre de rol vacío");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        Rol rol = rolRepository.findFirstByNombreIgnoreCase(buscado)
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setRolId(UUID.randomUUID());
                    r.setNombre(buscado.substring(0, 1).toUpperCase() + buscado.substring(1).toLowerCase());
                    r.setDescripcion("Rol creado automáticamente");
                    r.setActivo(true);
                    return rolRepository.save(r);
                });

        boolean yaTiene = usuarioRolRepository.existsByUsuario_UsuarioIdAndRol_RolId(usuarioId, rol.getRolId());
        if (yaTiene) {
            UsuarioRol existente = usuarioRolRepository
                    .findByUsuario_UsuarioIdAndRol_RolId(usuarioId, rol.getRolId())
                    .orElseThrow();
            UsuarioRolDTO dto = new UsuarioRolDTO(
                    existente.getUsuarioRolId(),
                    usuarioId,
                    rol.getRolId(),
                    rol.getNombre()
            );
            if (usuario.getPersona() != null) {
                dto.setNombreUsuario(usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido());
            }
            return dto;
        }

        UsuarioRol ur = new UsuarioRol();
        ur.setUsuarioRolId(UUID.randomUUID());
        ur.setUsuario(usuario);
        ur.setRol(rol);
        ur.setActivo(true);
        usuarioRolRepository.save(ur);

        UsuarioRolDTO dto = new UsuarioRolDTO(
                ur.getUsuarioRolId(),
                usuarioId,
                rol.getRolId(),
                rol.getNombre()
        );
        if (usuario.getPersona() != null) {
            dto.setNombreUsuario(usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido());
        }
        return dto;
    }
}
