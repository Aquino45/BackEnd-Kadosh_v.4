package kado.kadosh.repository;

import kado.kadosh.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonaRepository extends JpaRepository<Persona, UUID> {
    boolean existsByDni(String dni);
    Optional<Persona> findByDniAndActivoIsTrue(String dni);
}
