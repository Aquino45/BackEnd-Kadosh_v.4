package kado.kadosh.scheduler;

import kado.kadosh.entities.Persona;
import kado.kadosh.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ActualizadorEdadesJob {

    private final PersonaRepository personaRepository;

    // Cron: Se ejecuta todos los días a las 3:00 AM
    // Formato: segundo minuto hora dia mes dia-semana
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void actualizarEdadesMasivas() {
        System.out.println("🤖 ROBOT: Iniciando actualización de edades...");

        // 1. Traer todas las personas que tienen fecha de nacimiento
        // (Podrías optimizar esto con una Query para traer solo los que cumplen años hoy,
        // pero para empezar, revisar todos es más seguro y fácil).
        List<Persona> personas = personaRepository.findAll();

        int cont = 0;
        LocalDate hoy = LocalDate.now();

        for (Persona p : personas) {
            if (p.getFechaNacimiento() != null) {
                // Calculamos la edad REAL que debería tener hoy
                int edadReal = Period.between(p.getFechaNacimiento(), hoy).getYears();

                // Si la edad en la BD es diferente a la real (o es nula)
                if (p.getEdad() == null || p.getEdad() != edadReal) {
                    p.setEdad(edadReal);
                    // Al estar en una transacción, Hibernate detecta el cambio y hará el UPDATE solo
                    cont++;
                }
            }
        }

        if (cont > 0) {
            // Guardamos todos los cambios (si hubo)
            personaRepository.saveAll(personas);
        }

        System.out.println("🤖 ROBOT: Fin del proceso. Se actualizaron " + cont + " personas.");
    }
}