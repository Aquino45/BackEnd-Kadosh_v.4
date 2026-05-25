package kado.kadosh.repository;

import kado.kadosh.entities.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
