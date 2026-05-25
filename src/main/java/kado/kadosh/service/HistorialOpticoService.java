package kado.kadosh.service;

import kado.kadosh.dto.HistorialOpticoDTO; // <--- Importante: Importa el DTO
import kado.kadosh.entities.HistorialOptico;
import kado.kadosh.entities.Usuario;

import java.util.List;
import java.util.UUID;

public interface HistorialOpticoService {

    HistorialOptico crearHistorial(HistorialOpticoDTO dto);

    HistorialOptico actualizarHistorial(UUID id, HistorialOpticoDTO dto);

    /**
     * Obtiene la lista de todos los historiales de un paciente específico.
     */
    // 🔴 ANTES (Entity): List<HistorialOptico> listarTodoPorUsuario(UUID usuarioId);

    // 🟢 AHORA (DTO): Tienes que cambiarlo a esto:
    List<HistorialOpticoDTO> listarTodoPorUsuario(UUID usuarioId);

    List<Usuario> listarUsuariosConHistorial();

    void eliminarHistorial(UUID id);
}