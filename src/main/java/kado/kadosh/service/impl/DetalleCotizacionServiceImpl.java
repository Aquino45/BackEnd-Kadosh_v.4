package kado.kadosh.service.impl;

import kado.kadosh.entities.DetalleCotizacion;
import kado.kadosh.repository.DetalleCotizacionRepository;
import kado.kadosh.service.DetalleCotizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class DetalleCotizacionServiceImpl implements DetalleCotizacionService {

    @Autowired
    private DetalleCotizacionRepository detalleRepo;

    @Override
    public List<DetalleCotizacion> listarPorCotizacion(UUID cotizacionId) {
        // Trae todos los productos asociados a una venta específica (Lunas, Carey, etc.)
        return detalleRepo.findByCotizacionCotizacionId(cotizacionId);
    }

    @Override
    public List<DetalleCotizacion> listarPorProducto(UUID productoId) {
        // ✅ CORREGIDO: Sin guion bajo para que JPA no se maree
        return detalleRepo.findByProductoProductoId(productoId);
    }
}