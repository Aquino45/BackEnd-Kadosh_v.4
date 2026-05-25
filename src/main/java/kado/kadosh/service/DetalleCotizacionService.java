package kado.kadosh.service;

import kado.kadosh.entities.DetalleCotizacion;
import java.util.List;
import java.util.UUID;

public interface DetalleCotizacionService {
    // Para ver qué productos lleva una cotización específica
    List<DetalleCotizacion> listarPorCotizacion(UUID cotizacionId);

    // Para auditoría o reportes de ventas
    List<DetalleCotizacion> listarPorProducto(UUID productoId);
}