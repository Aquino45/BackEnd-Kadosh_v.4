package kado.kadosh.repository;

import kado.kadosh.entities.Cotizacion;
import kado.kadosh.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, UUID> {

    // ✅ ESTE ES EL ÚNICO QUE DEBE QUEDAR (el que usa CreatedAt)
    List<Cotizacion> findByUsuarioUsuarioIdOrderByCreatedAtDesc(UUID usuarioId);

    // Para el Admin: Ver qué cotizaciones faltan pagar
    List<Cotizacion> findByEstadoPagoFalseAndActivoTrue();

    // Filtra usuarios únicos que tienen al menos una cotización activa
    @Query("SELECT DISTINCT c.usuario FROM Cotizacion c WHERE c.activo = true")
    List<Usuario> findUsuariosConCotizacion();

}