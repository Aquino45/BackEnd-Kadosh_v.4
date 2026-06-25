package kado.kadosh.controller;

import kado.kadosh.dto.AjusteStockRequestDTO;
import kado.kadosh.dto.MovimientoStockDTO;
import kado.kadosh.dto.ProductoDTO;
import kado.kadosh.entities.MovimientoStock;
import kado.kadosh.entities.Producto;
import kado.kadosh.enums.TipoMovimientoStock;
import kado.kadosh.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // ──────────────────────────────────────────────────────────────
    // CONSULTA (cualquier usuario autenticado)
    // ──────────────────────────────────────────────────────────────

    @GetMapping
    public List<ProductoDTO> listarTodos() {
        return productoService.listarTodos().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductoDTO buscarPorId(@PathVariable UUID id) {
        return toDTO(productoService.buscarPorId(id));
    }

    @GetMapping("/categoria/{categoriaId}")
    public List<ProductoDTO> listarPorCategoria(@PathVariable UUID categoriaId) {
        return productoService.listarPorCategoria(categoriaId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/subcategoria/{subId}")
    public List<ProductoDTO> listarPorSub(@PathVariable String subId) {
        UUID uuid = UUID.fromString(subId);
        return productoService.listarPorSubCategoria(uuid).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/buscar")
    public List<ProductoDTO> buscarConFiltros(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) UUID subCategoriaId,
            @RequestParam(required = false) Boolean activo) {
        return productoService.buscarConFiltros(nombre, categoriaId, subCategoriaId, activo)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/stock-bajo")
    @PreAuthorize("hasAnyRole('Admin','Optometrista')")
    public List<ProductoDTO> listarStockBajo(@RequestParam(required = false) Integer limite) {
        return productoService.listarStockBajo(limite).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────────────────────
    // GESTIÓN DE PRODUCTOS (ADMIN, OPTICO)
    // ──────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','Optometrista')")
    public ResponseEntity<ProductoDTO> crear(@RequestBody ProductoDTO dto) {
        Producto creado = productoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Optometrista')")
    public ProductoDTO actualizar(@PathVariable UUID id, @RequestBody ProductoDTO dto) {
        return toDTO(productoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable UUID id) {
        productoService.eliminarLogico(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Producto eliminado correctamente",
                "idEliminado", id.toString()
        ));
    }

    // ──────────────────────────────────────────────────────────────
    // KARDEX / MOVIMIENTOS DE STOCK (ADMIN, OPTICO)
    // ──────────────────────────────────────────────────────────────

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('Admin','Optometrista')")
    public ProductoDTO ajustarStock(@PathVariable UUID id, @RequestBody AjusteStockRequestDTO dto) {
        TipoMovimientoStock tipo = TipoMovimientoStock.valueOf(dto.getTipo().toUpperCase());
        Producto actualizado = productoService.ajustarStock(id, dto.getCantidad(), tipo, dto.getMotivo());
        return toDTO(actualizado);
    }

    @GetMapping("/{id}/movimientos")
    @PreAuthorize("hasAnyRole('Admin','Optometrista')")
    public List<MovimientoStockDTO> historialStock(@PathVariable UUID id) {
        return productoService.historialStock(id).stream().map(this::toMovDTO).collect(Collectors.toList());
    }

    @GetMapping("/movimientos")
    @PreAuthorize("hasAnyRole('Admin','Optometrista')")
    public List<MovimientoStockDTO> historialStockGeneral() {
        return productoService.historialStockGeneral().stream().map(this::toMovDTO).collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────────────────────
    // MAPPERS
    // ──────────────────────────────────────────────────────────────

    private ProductoDTO toDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setProductoId(p.getProductoId());
        dto.setNombre(p.getNombre());
        dto.setPrecioActual(p.getPrecioActual());
        dto.setStock(p.getStock());
        dto.setActivo(p.getActivo());
        if (p.getCategoria() != null) {
            dto.setCategoriaId(p.getCategoria().getCategoriaId());
            dto.setNombreCategoria(p.getCategoria().getNombre());
        }
        if (p.getSubCategoria() != null) {
            dto.setSubCategoriaId(p.getSubCategoria().getSubCategoriaId());
            dto.setNombreSubCategoria(p.getSubCategoria().getNombre());
        }
        return dto;
    }

    private MovimientoStockDTO toMovDTO(MovimientoStock m) {
        MovimientoStockDTO dto = new MovimientoStockDTO();
        dto.setMovimientoId(m.getMovimientoId());
        dto.setProductoId(m.getProducto().getProductoId());
        dto.setNombreProducto(m.getProducto().getNombre());
        dto.setTipo(m.getTipo().name());
        dto.setCantidad(m.getCantidad());
        dto.setStockAnterior(m.getStockAnterior());
        dto.setStockNuevo(m.getStockNuevo());
        dto.setMotivo(m.getMotivo());
        dto.setUsuarioId(m.getUsuarioId());
        dto.setFecha(m.getFecha());
        return dto;
    }
}
