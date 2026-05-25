package kado.kadosh.service.impl;

import kado.kadosh.entities.Persona;
import kado.kadosh.repository.PersonaRepository;
import kado.kadosh.service.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;

    @Override
    public boolean existsByDni(String dni) {
        return personaRepository.existsByDni(dni);
    }

    @Override
    public Persona save(Persona persona) {

        if (persona.getFechaNacimiento() != null) {
            persona.setEdad(calcularEdad(persona.getFechaNacimiento()));
        }
        return personaRepository.save(persona);
    }

    @Override
    public Optional<Persona> findByDniActivo(String dni) {
        return personaRepository.findByDniAndActivoIsTrue(dni);
    }

    // ==========================
    // Helper para edad
    // ==========================
    private Integer calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return null;
        LocalDate hoy = LocalDate.now();
        if (fechaNacimiento.isAfter(hoy)) {

            return 0;
        }
        return Period.between(fechaNacimiento, hoy).getYears();
    }
}
