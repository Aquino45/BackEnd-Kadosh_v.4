package kado.kadosh.service;

import kado.kadosh.dto.RefraccionDescripcionDatosDTO;

import java.util.List;
import java.util.UUID;

public interface RefraccionDescripcionDatosService {

    List<RefraccionDescripcionDatosDTO> findAll();

    RefraccionDescripcionDatosDTO findById(UUID id);

    RefraccionDescripcionDatosDTO create(RefraccionDescripcionDatosDTO dto);

    RefraccionDescripcionDatosDTO update(UUID id, RefraccionDescripcionDatosDTO dto);

    void delete(UUID id);
}
