package kado.kadosh.repository;

import kado.kadosh.dto.ReporteCotizacionDTO;
import kado.kadosh.entities.Cotizacion;
import kado.kadosh.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, UUID> {

    // ✅ ESTE ES EL ÚNICO QUE DEBE QUEDAR (el que usa CreatedAt)
    List<Cotizacion> findByUsuarioUsuarioIdOrderByCreatedAtDesc(UUID usuarioId);

    // Para el Admin: Ver qué cotizaciones faltan pagar
    List<Cotizacion> findByEstadoPagoFalseAndActivoTrue();

    // Filtra usuarios únicos que tienen al menos una cotización activa
    @Query("SELECT DISTINCT c.usuario FROM Cotizacion c WHERE c.activo = true")
    List<Usuario> findUsuariosConCotizacion();

    @Query("SELECT new kado.kadosh.dto.ReporteCotizacionDTO(" +
            "c.cotizacionId, c.fechaCreacion, u.usuarioId, CONCAT(p.nombre, ' ', p.apellido), p.dni, " +
            "c.edad, c.total, c.estadoPago, c.fechaPago) " +
            "FROM Cotizacion c " +
            "LEFT JOIN c.usuario u " +
            "LEFT JOIN u.persona p " +
            "WHERE c.activo = true " +
            "AND (:fechaInicio IS NULL OR c.fechaCreacion >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR c.fechaCreacion <= :fechaFin) " +
            "AND (:usuarioId IS NULL OR u.usuarioId = :usuarioId) " +
            "AND (:estadoPago IS NULL OR c.estadoPago = :estadoPago) " +
            "ORDER BY c.fechaCreacion DESC")
    List<ReporteCotizacionDTO> reporteCotizaciones(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("usuarioId") UUID usuarioId,
            @Param("estadoPago") Boolean estadoPago);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.activo = true " +
            "AND (:fechaInicio IS NULL OR c.fechaCreacion >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR c.fechaCreacion <= :fechaFin)")
    Long contarCotizacionesReporte(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.activo = true AND c.estadoPago = true " +
            "AND (:fechaInicio IS NULL OR c.fechaCreacion >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR c.fechaCreacion <= :fechaFin)")
    Long contarCotizacionesPagadasReporte(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.activo = true AND c.estadoPago = false " +
            "AND (:fechaInicio IS NULL OR c.fechaCreacion >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR c.fechaCreacion <= :fechaFin)")
    Long contarCotizacionesPendientesReporte(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}