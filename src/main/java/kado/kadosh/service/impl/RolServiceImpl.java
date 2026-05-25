package kado.kadosh.service.impl;

import kado.kadosh.entities.Rol;
import kado.kadosh.repository.RolRepository;
import kado.kadosh.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    @Override
    public Rol save(Rol rol) {

        if (rol.getNombre() != null) {
            rol.setNombre(rol.getNombre().trim());
        }


        if (rol.getRolId() == null) {
            rol.setRolId(UUID.randomUUID());
        }


        if (rol.getActivo() == null) {
            rol.setActivo(true);
        }

        return rolRepository.save(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> findById(UUID id) {
        return rolRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> findByNombre(String nombre) {
        return rolRepository.findByNombreIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> listarActivos() {
        return rolRepository.listarActivos();
    }

    @Override
    public void deleteById(UUID id) {
        if (!rolRepository.existsById(id)) {
            throw new IllegalArgumentException("El rol con ID " + id + " no existe");
        }
        rolRepository.deleteById(id);
    }
}
