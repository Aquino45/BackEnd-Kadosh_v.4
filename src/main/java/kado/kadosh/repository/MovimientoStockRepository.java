package kado.kadosh.repository;

import kado.kadosh.entities.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, UUID> {

    List<MovimientoStock> findByProducto_ProductoIdOrderByFechaDesc(UUID productoId);

    List<MovimientoStock> findAllByOrderByFechaDesc();
}
