package kado.kadosh.repository;

import kado.kadosh.entities.HistorialConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistorialConsultaRepository extends JpaRepository<HistorialConsulta, UUID> {

    List<HistorialConsulta> findAllByOrderByFechaConsultaDesc();

    List<HistorialConsulta> findByRespuestaEncontradaFalseOrderByFechaConsultaDesc();
}
