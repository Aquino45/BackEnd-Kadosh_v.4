package kado.kadosh.repository;

import kado.kadosh.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {

    // 1. Nivel Maestro: Todos los productos de una categoría (ej. Monturas)
    List<Producto> findByCategoria_CategoriaIdAndActivoTrue(UUID categoriaId);

    // 2. ✅ CORREGIDO: Naming exacto para SubCategoria (con C mayúscula)
    // Este nombre debe coincidir letra por letra con el que pusiste en tu Entidad Producto
    List<Producto> findBySubCategoria_SubCategoriaIdAndActivoTrue(UUID subCategoriaId);

    // 3. Inventario: Ver qué modelos (Carey, Metal, etc.) se están agotando
    List<Producto> findByStockLessThanEqual(Integer limite);
}