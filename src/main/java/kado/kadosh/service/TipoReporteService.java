package kado.kadosh.service;

import kado.kadosh.entities.TipoReporte;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoReporteService {

    TipoReporte save(TipoReporte tipoReporte);

    Optional<TipoReporte> findById(UUID id);

    Optional<TipoReporte> findByCodigo(String codigo);

    Optional<TipoReporte> findByNombre(String nombre);

    List<TipoReporte> listar();

    List<TipoReporte> listarActivos();

    void deleteById(UUID id);

    TipoReporte desactivar(UUID id);
}