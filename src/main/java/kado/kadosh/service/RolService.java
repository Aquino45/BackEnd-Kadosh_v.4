package kado.kadosh.service;

import kado.kadosh.entities.Rol;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RolService {
    Rol save(Rol rol);
    Optional<Rol> findById(UUID id);
    Optional<Rol> findByNombre(String nombre);
    List<Rol> listar();
    List<Rol> listarActivos();
    void deleteById(UUID id);
}
