package kado.kadosh.service.impl;

import kado.kadosh.entities.TipoReporte;
import kado.kadosh.repository.TipoReporteRepository;
import kado.kadosh.service.TipoReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoReporteServiceImpl implements TipoReporteService {

    private final TipoReporteRepository tipoReporteRepository;

    @Override
    public TipoReporte save(TipoReporte tipoReporte) {

        if (tipoReporte.getTipoReporteId() == null) {
            tipoReporte.setTipoReporteId(UUID.randomUUID());
        }

        if (tipoReporte.getCodigo() != null) {
            tipoReporte.setCodigo(tipoReporte.getCodigo().trim().toUpperCase());
        }

        if (tipoReporte.getNombre() != null) {
            tipoReporte.setNombre(tipoReporte.getNombre().trim());
        }

        if (tipoReporte.getPermitePdf() == null) {
            tipoReporte.setPermitePdf(true);
        }

        if (tipoReporte.getPermiteExcel() == null) {
            tipoReporte.setPermiteExcel(true);
        }

        if (tipoReporte.getActivo() == null) {
            tipoReporte.setActivo(true);
        }

        return tipoReporteRepository.save(tipoReporte);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoReporte> findById(UUID id) {
        return tipoReporteRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoReporte> findByCodigo(String codigo) {
        return tipoReporteRepository.findByCodigoIgnoreCase(codigo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoReporte> findByNombre(String nombre) {
        return tipoReporteRepository.findByNombreIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoReporte> listar() {
        return tipoReporteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoReporte> listarActivos() {
        return tipoReporteRepository.listarActivos();
    }

    @Override
    public void deleteById(UUID id) {
        if (!tipoReporteRepository.existsById(id)) {
            throw new IllegalArgumentException("El tipo de reporte con ID " + id + " no existe");
        }
        tipoReporteRepository.deleteById(id);
    }

    @Override
    public TipoReporte desactivar(UUID id) {
        TipoReporte tipoReporte = tipoReporteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de reporte no encontrado"));

        tipoReporte.setActivo(false);
        return tipoReporteRepository.save(tipoReporte);
    }
}