package kado.kadosh.repository;

import kado.kadosh.entities.HistorialOptico;
import kado.kadosh.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistorialOpticoRepository extends JpaRepository<HistorialOptico, UUID> {

    // Para ver todos los historiales de UN usuario específico, ordenados por fecha
    List<HistorialOptico> findByUsuarioUsuarioIdOrderByFechaDesc(UUID usuarioId);

    // Para saber si un usuario ya tiene al menos un historial
    boolean existsByUsuarioUsuarioId(UUID usuarioId);

    // Para listar quiénes ya tienen historial (sin repetir usuarios)
    @Query("SELECT DISTINCT h.usuario FROM HistorialOptico h JOIN FETCH h.usuario.persona")
    List<Usuario> findUsuariosConHistorial();
}