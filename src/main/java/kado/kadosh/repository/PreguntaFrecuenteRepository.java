package kado.kadosh.repository;

import kado.kadosh.entities.PreguntaFrecuente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PreguntaFrecuenteRepository extends JpaRepository<PreguntaFrecuente, UUID> {

    List<PreguntaFrecuente> findByActivoTrue();

    @Query("SELECT p FROM PreguntaFrecuente p WHERE p.activo = true AND (" +
            "LOWER(p.pregunta) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.palabrasClave) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<PreguntaFrecuente> buscarPorKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM PreguntaFrecuente p WHERE " +
            "(:q IS NULL OR LOWER(p.pregunta) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.respuesta) LIKE LOWER(CONCAT('%', :q, '%'))) " +
            "AND (:categoria IS NULL OR p.categoria = :categoria) " +
            "AND (:activo IS NULL OR p.activo = :activo) " +
            "ORDER BY p.creadoEn DESC")
    List<PreguntaFrecuente> buscarConFiltros(
            @Param("q") String q,
            @Param("categoria") String categoria,
            @Param("activo") Boolean activo);

    @Query("SELECT DISTINCT p.categoria FROM PreguntaFrecuente p WHERE p.categoria IS NOT NULL AND p.activo = true ORDER BY p.categoria")
    List<String> findDistinctCategorias();
}
