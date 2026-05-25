package kado.kadosh.repository;

import kado.kadosh.entities.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, UUID> {

    // 1. Para listar todos los productos de una cotización específica (Lunas + Montura)
    List<DetalleCotizacion> findByCotizacionCotizacionId(UUID cotizacionId);

    // 2. Método corregido para buscar por ID de producto (Sin el guion bajo que da error)
    // Asegúrate de que en tu Entidad el objeto se llame "producto"
    List<DetalleCotizacion> findByProductoProductoId(UUID productoId);
}