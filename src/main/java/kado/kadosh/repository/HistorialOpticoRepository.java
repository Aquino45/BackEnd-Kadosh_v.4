package kado.kadosh.repository;

import kado.kadosh.dto.ReporteHistorialOpticoDTO;
import kado.kadosh.entities.HistorialOptico;
import kado.kadosh.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query("SELECT new kado.kadosh.dto.ReporteHistorialOpticoDTO(" +
            "h.historialOpticoId, h.fecha, u.usuarioId, CONCAT(p.nombre, ' ', p.apellido), p.dni, p.telefono, " +
            "h.edad, h.evaluador, h.recomendaciones) " +
            "FROM HistorialOptico h " +
            "LEFT JOIN h.usuario u " +
            "LEFT JOIN u.persona p " +
            "WHERE (:fechaInicio IS NULL OR h.fecha >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR h.fecha <= :fechaFin) " +
            "AND (:usuarioId IS NULL OR u.usuarioId = :usuarioId) " +
            "ORDER BY h.fecha DESC")
    List<ReporteHistorialOpticoDTO> reporteHistorialOptico(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("usuarioId") UUID usuarioId);

    @Query("SELECT COUNT(h) FROM HistorialOptico h WHERE " +
            "(:fechaInicio IS NULL OR h.fecha >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR h.fecha <= :fechaFin)")
    Long contarHistorialesReporte(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}