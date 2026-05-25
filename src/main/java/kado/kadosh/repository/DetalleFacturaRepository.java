package kado.kadosh.repository;

import kado.kadosh.entities.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, UUID> {
    List<DetalleFactura> findByFacturaFacturaId(UUID facturaId);
}
