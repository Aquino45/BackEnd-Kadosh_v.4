package kado.kadosh.service.impl;

import kado.kadosh.dto.FacturaRequestDTO;
import kado.kadosh.entities.*;
import kado.kadosh.repository.CotizacionRepository;
import kado.kadosh.repository.DetalleFacturaRepository;
import kado.kadosh.repository.FacturaRepository;
import kado.kadosh.service.FacturaService;
import kado.kadosh.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private DetalleFacturaRepository detalleFacturaRepository;

    @Autowired
    private ProductoService productoService;

    @Override
    public Factura emitir(FacturaRequestDTO dto) {
        Cotizacion cotizacion = cotizacionRepository.findById(dto.getCotizacionId())
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        if (!Boolean.TRUE.equals(cotizacion.getEstadoPago())) {
            throw new RuntimeException("La cotización no ha sido pagada aún");
        }

        if (facturaRepository.findByCotizacionCotizacionId(dto.getCotizacionId()).isPresent()) {
            throw new RuntimeException("Ya existe una factura emitida para esta cotización");
        }

        List<DetalleCotizacion> detallesCotizacion = cotizacion.getDetalles().stream()
                .filter(d -> Boolean.TRUE.equals(d.getActivo()))
                .collect(Collectors.toList());

        if (detallesCotizacion.isEmpty()) {
            throw new RuntimeException("La cotización no tiene detalles activos");
        }

        double subtotal = detallesCotizacion.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioCongelado())
                .sum();
        double igv = Math.round(subtotal * 0.18 * 100.0) / 100.0;
        double total = Math.round((subtotal + igv) * 100.0) / 100.0;

        Factura factura = new Factura();
        factura.setFacturaId(UUID.randomUUID());
        factura.setNumero(generarNumero());
        factura.setCotizacion(cotizacion);
        factura.setUsuario(cotizacion.getUsuario());
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubtotal(subtotal);
        factura.setIgv(igv);
        factura.setTotal(total);
        factura.setEstado("EMITIDA");
        factura.setActivo(true);

        Factura saved = facturaRepository.save(factura);

        List<DetalleFactura> detalles = detallesCotizacion.stream().map(dc -> {
            DetalleFactura df = new DetalleFactura();
            df.setDetalleFacturaId(UUID.randomUUID());
            df.setFactura(saved);
            df.setProducto(dc.getProducto());
            df.setDescripcion(dc.getDescripcion());
            df.setCantidad(dc.getCantidad());
            df.setPrecioUnitario(dc.getPrecioCongelado());
            df.setSubtotal(dc.getCantidad() * dc.getPrecioCongelado());
            df.setActivo(true);
            return df;
        }).collect(Collectors.toList());

        detalleFacturaRepository.saveAll(detalles);

        return facturaRepository.findById(saved.getFacturaId()).orElse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> listar() {
        return facturaRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Factura buscarPorId(UUID id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> buscarPorUsuario(UUID usuarioId) {
        return facturaRepository.findByUsuarioUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> buscarPorEstado(String estado) {
        return facturaRepository.findByEstado(estado);
    }

    @Override
    public void anular(UUID facturaId) {
        Factura factura = buscarPorId(facturaId);
        if ("ANULADA".equals(factura.getEstado())) {
            throw new RuntimeException("La factura ya se encuentra anulada");
        }

        // 1. Anular detalles y restaurar stock de productos
        if (factura.getDetalles() != null) {
            for (DetalleFactura d : factura.getDetalles()) {
                d.setActivo(false);
                if (d.getProducto() != null && d.getCantidad() != null) {
                    productoService.restaurarStock(d.getProducto().getProductoId(), d.getCantidad());
                }
            }
            detalleFacturaRepository.saveAll(factura.getDetalles());
        }

        // 2. Revertir pago de la cotización
        if (factura.getCotizacion() != null) {
            Cotizacion coti = factura.getCotizacion();
            coti.setEstadoPago(false);
            coti.setFechaPago(null);
            cotizacionRepository.save(coti);
        }

        // 3. Anular la factura
        factura.setEstado("ANULADA");
        factura.setActivo(false);
        facturaRepository.save(factura);
    }

    private String generarNumero() {
        long count = facturaRepository.count() + 1;
        return String.format("FAC-%06d", count);
    }
}
