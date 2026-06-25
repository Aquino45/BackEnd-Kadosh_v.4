package kado.kadosh.service;

import kado.kadosh.dto.ProductoDTO;
import kado.kadosh.entities.MovimientoStock;
import kado.kadosh.entities.Producto;
import kado.kadosh.enums.TipoMovimientoStock;

import java.util.List;
import java.util.UUID;

public interface ProductoService {

    List<Producto> listarTodos();

    // Nivel 1: Ver todo lo de "Monturas"
    List<Producto> listarPorCategoria(UUID categoriaId);

    // "SubCategoria" con C mayúscula para que coincida con el Impl
    List<Producto> listarPorSubCategoria(UUID subCategoriaId);

    List<Producto> buscarConFiltros(String nombre, UUID categoriaId, UUID subCategoriaId, Boolean activo);

    List<Producto> listarStockBajo(Integer limite);

    Producto buscarPorId(UUID id);

    Producto crear(ProductoDTO dto);

    Producto actualizar(UUID id, ProductoDTO dto);

    void eliminarLogico(UUID id);

    // Método para descontar stock al generar la cotización
    void reducirStock(UUID productoId, Integer cantidad);

    void restaurarStock(UUID productoId, Integer cantidad);

    // Ajuste manual de inventario (entrada/salida/ajuste físico), con auditoría
    Producto ajustarStock(UUID productoId, Integer cantidad, TipoMovimientoStock tipo, String motivo);

    List<MovimientoStock> historialStock(UUID productoId);

    List<MovimientoStock> historialStockGeneral();
}
