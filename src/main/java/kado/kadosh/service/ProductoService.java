package kado.kadosh.service;

import kado.kadosh.entities.Producto;
import java.util.List;
import java.util.UUID;

public interface ProductoService {

    // Nivel 1: Ver todo lo de "Monturas"
    List<Producto> listarPorCategoria(UUID categoriaId);

    // ✅ CORREGIDO: "SubCategoria" con C mayúscula para que coincida con el Impl
    List<Producto> listarPorSubCategoria(UUID subCategoriaId);

    Producto buscarPorId(UUID id);

    Producto guardar(Producto producto);

    // Método para descontar stock al generar la cotización
    void reducirStock(UUID productoId, Integer cantidad);

    void restaurarStock(UUID productoId, Integer cantidad);
}