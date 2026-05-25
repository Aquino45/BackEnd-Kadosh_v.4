package kado.kadosh.service.impl;

import kado.kadosh.dto.CotizacionRequestDTO;
import kado.kadosh.entities.*;
import kado.kadosh.repository.*;
import kado.kadosh.service.CotizacionService;
import kado.kadosh.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class CotizacionServiceImpl implements CotizacionService {

    @Autowired private CotizacionRepository cotizacionRepo;
    @Autowired private DetalleCotizacionRepository detalleRepo;
    @Autowired private ProductoService productoService;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private HistorialOpticoRepository historialRepo;

    // El formateador para la fecha que mandas desde el front
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    @Transactional
    public Cotizacion crearCotizacion(CotizacionRequestDTO dto) {

        // 1. Validaciones
        Usuario cliente = usuarioRepo.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        HistorialOptico historial = historialRepo.findById(dto.getHistorialId())
                .orElseThrow(() -> new RuntimeException("Historial médico no encontrado"));

        if (!historial.getUsuario().getUsuarioId().equals(cliente.getUsuarioId())) {
            throw new RuntimeException("¡ERROR DE INTEGRIDAD! El historial médico pertenece a otro usuario.");
        }

        // 2. Crear y persistir la Cabecera
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setCotizacionId(UUID.randomUUID());
        cotizacion.setUsuario(cliente);
        cotizacion.setHistorialOptico(historial);
        cotizacion.setEdad(cliente.getPersona().getEdad());

        // 🔥 AQUÍ SE AÑADE LA FECHA QUE MANDAS DESDE EL FRONT
        if (dto.getFechaCreacion() != null && !dto.getFechaCreacion().isBlank()) {
            cotizacion.setFechaCreacion(LocalDateTime.parse(dto.getFechaCreacion(), FORMATO_FECHA));
        } else {
            cotizacion.setFechaCreacion(LocalDateTime.now());
        }

        cotizacion.setTotal(0.0);
        cotizacion.setEstadoPago(false);
        cotizacion.setActivo(true);

        Cotizacion nuevaCoti = cotizacionRepo.save(cotizacion);

        double acumuladoTotal = 0.0;

        // 3. Procesar Productos
        for (var item : dto.getItems()) {
            Producto prod = productoService.buscarPorId(item.getProductoId());
            productoService.reducirStock(prod.getProductoId(), item.getCantidad());

            DetalleCotizacion detalle = new DetalleCotizacion();
            detalle.setDetalleId(UUID.randomUUID());
            detalle.setCotizacion(nuevaCoti);
            detalle.setProducto(prod);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioCongelado(prod.getPrecioActual());
            detalle.setActivo(true);

            detalleRepo.save(detalle);
            acumuladoTotal += (prod.getPrecioActual() * item.getCantidad());
        }

        // 4. Procesar Servicios (fabricación de lente, etc.)
        if (dto.getServicios() != null) {
            for (var srv : dto.getServicios()) {
                if (srv.getDescripcion() == null || srv.getPrecio() == null) continue;
                int cant = srv.getCantidad() != null ? srv.getCantidad() : 1;

                DetalleCotizacion servicio = new DetalleCotizacion();
                servicio.setDetalleId(UUID.randomUUID());
                servicio.setCotizacion(nuevaCoti);
                servicio.setProducto(null);
                servicio.setDescripcion(srv.getDescripcion());
                servicio.setCantidad(cant);
                servicio.setPrecioCongelado(srv.getPrecio());
                servicio.setActivo(true);

                detalleRepo.save(servicio);
                acumuladoTotal += srv.getPrecio() * cant;
            }
        }

        // 5. Actualizar el total final
        nuevaCoti.setTotal(acumuladoTotal);
        return cotizacionRepo.save(nuevaCoti);
    }

    @Override
    public List<Cotizacion> listarPorUsuario(UUID usuarioId) {
        // Se queda tal cual pediste en tu Repository
        return cotizacionRepo.findByUsuarioUsuarioIdOrderByCreatedAtDesc(usuarioId);
    }

    @Override
    public Cotizacion buscarPorId(UUID id) {
        return cotizacionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public void registrarPago(UUID cotizacionId) {
        Cotizacion coti = buscarPorId(cotizacionId);
        coti.setEstadoPago(true);
        coti.setFechaPago(LocalDateTime.now());
        cotizacionRepo.save(coti);
    }

    @Override
    public List<Usuario> listarUsuariosConCotizacion() {
        return cotizacionRepo.findUsuariosConCotizacion();
    }
}