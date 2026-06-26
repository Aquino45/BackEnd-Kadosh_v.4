package kado.kadosh.repository;

import kado.kadosh.entities.TipoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoReporteRepository extends JpaRepository<TipoReporte, UUID> {

    Optional<TipoReporte> findByCodigoIgnoreCase(String codigo);

    Optional<TipoReporte> findByNombreIgnoreCase(String nombre);

    @Query("SELECT t FROM TipoReporte t WHERE t.activo = true ORDER BY t.nombre")
    List<TipoReporte> listarActivos();
}
