package kado.kadosh.repository;

import kado.kadosh.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RolRepository extends JpaRepository<Rol, UUID> {


    Optional<Rol> findByNombreIgnoreCase(String nombre);


    @Query("SELECT r FROM Rol r WHERE r.activo = true ORDER BY r.nombre ASC")
    List<Rol> listarActivos();
    Optional<Rol> findFirstByNombreIgnoreCase(String nombre);
}
