package kado.kadosh.service.impl;

import kado.kadosh.dto.ProductoDTO;
import kado.kadosh.entities.Categoria;
import kado.kadosh.entities.MovimientoStock;
import kado.kadosh.entities.Producto;
import kado.kadosh.entities.SubCategoria;
import kado.kadosh.enums.TipoMovimientoStock;
import kado.kadosh.repository.CategoriaRepository;
import kado.kadosh.repository.MovimientoStockRepository;
import kado.kadosh.repository.ProductoRepository;
import kado.kadosh.repository.SubCategoriaRepository;
import kado.kadosh.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepo;
    private final CategoriaRepository categoriaRepo;
    private final SubCategoriaRepository subRepo;
    private final MovimientoStockRepository movimientoRepo;

    @Override
    public List<Producto> listarTodos() {
        return productoRepo.findAll();
    }

    @Override
    public List<Producto> listarPorCategoria(UUID categoriaId) {
        return productoRepo.findByCategoria_CategoriaIdAndActivoTrue(categoriaId);
    }

    @Override
    public List<Producto> listarPorSubCategoria(UUID subCategoriaId) {
        return productoRepo.findBySubCategoria_SubCategoriaIdAndActivoTrue(subCategoriaId);
    }

    @Override
    public List<Producto> buscarConFiltros(String nombre, UUID categoriaId, UUID subCategoriaId, Boolean activo) {
        String nombreNorm = (nombre != null && !nombre.isBlank()) ? nombre.trim() : null;
        return productoRepo.buscarConFiltros(nombreNorm, categoriaId, subCategoriaId, activo);
    }

    @Override
    public List<Producto> listarStockBajo(Integer limite) {
        int limiteFinal = (limite != null) ? limite : 5;
        return productoRepo.findByStockLessThanEqualAndActivoTrue(limiteFinal);
    }

    @Override
    public Producto buscarPorId(UUID id) {
        return productoRepo.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Producto crear(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setProductoId(UUID.randomUUID());
        producto.setActivo(true);
        aplicarDTO(producto, dto);
        return productoRepo.save(producto);
    }

    @Override
    @Transactional
    public Producto actualizar(UUID id, ProductoDTO dto) {
        Producto producto = buscarPorId(id);
        aplicarDTO(producto, dto);
        if (dto.getActivo() != null) {
            producto.setActivo(dto.getActivo());
        }
        return productoRepo.save(producto);
    }

    @Override
    @Transactional
    public void eliminarLogico(UUID id) {
        Producto producto = buscarPorId(id);
        producto.setActivo(false);
        productoRepo.save(producto);
    }

    @Override
    @Transactional
    public void reducirStock(UUID productoId, Integer cantidad) {
        Producto prod = buscarPorId(productoId);
        if (prod.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para el producto: " + prod.getNombre());
        }
        int anterior = prod.getStock();
        prod.setStock(anterior - cantidad);
        productoRepo.save(prod);
        registrarMovimiento(prod, TipoMovimientoStock.VENTA, cantidad, anterior, prod.getStock(), "Descuento por cotización");
    }

    @Override
    @Transactional
    public void restaurarStock(UUID productoId, Integer cantidad) {
        Producto prod = buscarPorId(productoId);
        int anterior = prod.getStock();
        prod.setStock(anterior + cantidad);
        productoRepo.save(prod);
        registrarMovimiento(prod, TipoMovimientoStock.ANULACION, cantidad, anterior, prod.getStock(), "Restitución por anulación de factura");
    }

    @Override
    @Transactional
    public Producto ajustarStock(UUID productoId, Integer cantidad, TipoMovimientoStock tipo, String motivo) {
        Producto prod = buscarPorId(productoId);
        int anterior = prod.getStock();
        int nuevo;

        switch (tipo) {
            case ENTRADA -> {
                if (cantidad <= 0) throw new RuntimeException("La cantidad de entrada debe ser positiva");
                nuevo = anterior + cantidad;
            }
            case SALIDA -> {
                if (cantidad <= 0) throw new RuntimeException("La cantidad de salida debe ser positiva");
                if (anterior < cantidad) throw new RuntimeException("Stock insuficiente para registrar la salida");
                nuevo = anterior - cantidad;
            }
            case AJUSTE -> nuevo = anterior + cantidad; // cantidad puede ser positiva o negativa
            default -> throw new RuntimeException("Tipo de movimiento no permitido para ajuste manual: " + tipo);
        }

        prod.setStock(nuevo);
        productoRepo.save(prod);
        registrarMovimiento(prod, tipo, cantidad, anterior, nuevo, motivo);
        return prod;
    }

    @Override
    public List<MovimientoStock> historialStock(UUID productoId) {
        return movimientoRepo.findByProducto_ProductoIdOrderByFechaDesc(productoId);
    }

    @Override
    public List<MovimientoStock> historialStockGeneral() {
        return movimientoRepo.findAllByOrderByFechaDesc();
    }

    private void aplicarDTO(Producto producto, ProductoDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del producto es obligatorio");
        }
        if (dto.getPrecioActual() == null || dto.getPrecioActual() < 0) {
            throw new RuntimeException("El precio debe ser un valor mayor o igual a 0");
        }
        if (dto.getStock() == null || dto.getStock() < 0) {
            throw new RuntimeException("El stock debe ser un valor mayor o igual a 0");
        }

        Categoria categoria = null;
        SubCategoria subCategoria = null;

        if (dto.getSubCategoriaId() != null) {
            subCategoria = subRepo.findById(dto.getSubCategoriaId())
                    .orElseThrow(() -> new RuntimeException("La subcategoría enviada no existe"));

            UUID idCatRealDeLaSub = subCategoria.getCategoria().getCategoriaId();
            if (dto.getCategoriaId() != null && !idCatRealDeLaSub.equals(dto.getCategoriaId())) {
                throw new RuntimeException("La subcategoría '" + subCategoria.getNombre() +
                        "' no pertenece a la categoría enviada");
            }
            categoria = subCategoria.getCategoria();
        } else if (dto.getCategoriaId() != null) {
            categoria = categoriaRepo.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("La categoría enviada no existe"));
        }

        producto.setNombre(dto.getNombre());
        producto.setPrecioActual(dto.getPrecioActual());
        producto.setStock(dto.getStock());
        producto.setCategoria(categoria);
        producto.setSubCategoria(subCategoria);
    }

    private void registrarMovimiento(Producto producto, TipoMovimientoStock tipo, int cantidad,
                                      int stockAnterior, int stockNuevo, String motivo) {
        try {
            MovimientoStock mov = new MovimientoStock();
            mov.setMovimientoId(UUID.randomUUID());
            mov.setProducto(producto);
            mov.setTipo(tipo);
            mov.setCantidad(cantidad);
            mov.setStockAnterior(stockAnterior);
            mov.setStockNuevo(stockNuevo);
            mov.setMotivo(motivo);
            mov.setFecha(LocalDateTime.now());

            try {
                String usuarioIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
                mov.setUsuarioId(UUID.fromString(usuarioIdStr));
            } catch (Exception ignored) {
                // Movimiento sin usuario identificable (ej. proceso interno)
            }

            movimientoRepo.save(mov);
        } catch (Exception ignored) {
            // El fallo al registrar el kardex no debe interrumpir la operación de stock
        }
    }
}
