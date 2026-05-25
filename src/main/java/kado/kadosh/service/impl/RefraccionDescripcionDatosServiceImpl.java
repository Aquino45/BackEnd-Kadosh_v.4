package kado.kadosh.service.impl;

import kado.kadosh.dto.RefraccionDescripcionDatosDTO;
import kado.kadosh.entities.RefraccionDescripcionDatos;
import kado.kadosh.repository.RefraccionDescripcionDatosRepository;
import kado.kadosh.service.RefraccionDescripcionDatosService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RefraccionDescripcionDatosServiceImpl implements RefraccionDescripcionDatosService {

    private final RefraccionDescripcionDatosRepository repository;

    public RefraccionDescripcionDatosServiceImpl(RefraccionDescripcionDatosRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RefraccionDescripcionDatosDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RefraccionDescripcionDatosDTO findById(UUID id) {
        RefraccionDescripcionDatos entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Refracción descripción no encontrada (id=" + id + ")"));
        return toDTO(entity);
    }

    @Override
    public RefraccionDescripcionDatosDTO create(RefraccionDescripcionDatosDTO dto) {
        RefraccionDescripcionDatos entity = new RefraccionDescripcionDatos();
        entity.setRefraccionDescripcionDatosId(UUID.randomUUID()); // 👈 RAW(16) como en todo Kadosh
        entity.setCode(dto.getCode());
        entity.setTitulo(dto.getTitulo());
        entity.setDescripcion(dto.getDescripcion());

        RefraccionDescripcionDatos saved = repository.save(entity);
        return toDTO(saved);
    }

    @Override
    public RefraccionDescripcionDatosDTO update(UUID id, RefraccionDescripcionDatosDTO dto) {
        RefraccionDescripcionDatos entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Refracción descripción no encontrada (id=" + id + ")"));

        entity.setCode(dto.getCode());
        entity.setTitulo(dto.getTitulo());
        entity.setDescripcion(dto.getDescripcion());

        RefraccionDescripcionDatos saved = repository.save(entity);
        return toDTO(saved);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Refracción descripción no encontrada (id=" + id + ")");
        }
        repository.deleteById(id);
    }

    // ====== Mapper ======

    private RefraccionDescripcionDatosDTO toDTO(RefraccionDescripcionDatos e) {
        RefraccionDescripcionDatosDTO dto = new RefraccionDescripcionDatosDTO();
        dto.setRefraccionDescripcionDatosId(e.getRefraccionDescripcionDatosId());
        dto.setCode(e.getCode());
        dto.setTitulo(e.getTitulo());
        dto.setDescripcion(e.getDescripcion());
        return dto;
    }
}
