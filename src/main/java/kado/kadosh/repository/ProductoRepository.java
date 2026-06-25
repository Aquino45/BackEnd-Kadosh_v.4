package kado.kadosh.repository;

import kado.kadosh.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {

    // 1. Nivel Maestro: Todos los productos de una categoría (ej. Monturas)
    List<Producto> findByCategoria_CategoriaIdAndActivoTrue(UUID categoriaId);

    // 2. Naming exacto para SubCategoria (con C mayúscula)
    List<Producto> findBySubCategoria_SubCategoriaIdAndActivoTrue(UUID subCategoriaId);

    // 3. Inventario: ver qué modelos se están agotando (solo activos)
    List<Producto> findByStockLessThanEqualAndActivoTrue(Integer limite);

    @Query("SELECT p FROM Producto p WHERE " +
            "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (:categoriaId IS NULL OR p.categoria.categoriaId = :categoriaId) " +
            "AND (:subCategoriaId IS NULL OR p.subCategoria.subCategoriaId = :subCategoriaId) " +
            "AND (:activo IS NULL OR p.activo = :activo) " +
            "ORDER BY p.nombre")
    List<Producto> buscarConFiltros(
            @Param("nombre") String nombre,
            @Param("categoriaId") UUID categoriaId,
            @Param("subCategoriaId") UUID subCategoriaId,
            @Param("activo") Boolean activo);
}
