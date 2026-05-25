package kado.kadosh.service;

import kado.kadosh.entities.Persona;

import java.util.Optional;

public interface PersonaService {
    boolean existsByDni(String dni);
    Persona save(Persona persona);
    Optional<Persona> findByDniActivo(String dni);
}
