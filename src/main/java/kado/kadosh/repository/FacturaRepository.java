package kado.kadosh.repository;

import kado.kadosh.dto.ReporteVentasDTO;
import kado.kadosh.entities.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, UUID> {
    Optional<Factura> findByNumero(String numero);
    List<Factura> findByActivoTrue();
    List<Factura> findByUsuarioUsuarioId(UUID usuarioId);
    List<Factura> findByEstado(String estado);
    Optional<Factura> findByCotizacionCotizacionId(UUID cotizacionId);

    @Query("SELECT new kado.kadosh.dto.ReporteVentasDTO(" +
            "f.facturaId, f.numero, f.fechaEmision, " +
            "u.usuarioId, CONCAT(p.nombre, ' ', p.apellido), p.dni, " +
            "f.subtotal, f.igv, f.total, f.estado) " +
            "FROM Factura f " +
            "LEFT JOIN f.usuario u " +
            "LEFT JOIN u.persona p " +
            "WHERE (:fechaInicio IS NULL OR f.fechaEmision >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR f.fechaEmision <= :fechaFin) " +
            "AND (:usuarioId IS NULL OR u.usuarioId = :usuarioId) " +
            "AND (:estado IS NULL OR f.estado = :estado) " +
            "ORDER BY f.fechaEmision DESC")
    List<ReporteVentasDTO> reporteVentas(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("usuarioId") UUID usuarioId,
            @Param("estado") String estado);

    @Query("SELECT COUNT(f) FROM Factura f WHERE " +
            "(:fechaInicio IS NULL OR f.fechaEmision >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR f.fechaEmision <= :fechaFin)")
    Long contarFacturasReporte(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COALESCE(SUM(f.total), 0) FROM Factura f WHERE " +
            "(:fechaInicio IS NULL OR f.fechaEmision >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR f.fechaEmision <= :fechaFin)")
    Double totalVendidoReporte(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}
