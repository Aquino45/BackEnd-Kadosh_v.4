package kado.kadosh.repository;

import kado.kadosh.dto.ReporteProductoVendidoDTO;
import kado.kadosh.entities.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, UUID> {
    List<DetalleFactura> findByFacturaFacturaId(UUID facturaId);

    @Query("SELECT new kado.kadosh.dto.ReporteProductoVendidoDTO(" +
            "pr.productoId, pr.nombre, cat.nombre, sub.nombre, pr.precioActual, " +
            "SUM(df.cantidad), SUM(df.subtotal)) " +
            "FROM DetalleFactura df " +
            "JOIN df.producto pr " +
            "LEFT JOIN pr.categoria cat " +
            "LEFT JOIN pr.subCategoria sub " +
            "JOIN df.factura f " +
            "WHERE df.activo = true " +
            "AND (:fechaInicio IS NULL OR f.fechaEmision >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR f.fechaEmision <= :fechaFin) " +
            "AND (:categoriaId IS NULL OR cat.categoriaId = :categoriaId) " +
            "AND (:subCategoriaId IS NULL OR sub.subCategoriaId = :subCategoriaId) " +
            "GROUP BY pr.productoId, pr.nombre, cat.nombre, sub.nombre, pr.precioActual " +
            "ORDER BY SUM(df.subtotal) DESC")
    List<ReporteProductoVendidoDTO> reporteProductosVendidos(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("categoriaId") UUID categoriaId,
            @Param("subCategoriaId") UUID subCategoriaId);

    @Query("SELECT COALESCE(SUM(df.cantidad), 0) FROM DetalleFactura df JOIN df.factura f " +
            "WHERE df.activo = true " +
            "AND (:fechaInicio IS NULL OR f.fechaEmision >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR f.fechaEmision <= :fechaFin)")
    Long contarProductosVendidosReporte(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}
