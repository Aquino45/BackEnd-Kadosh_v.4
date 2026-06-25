package kado.kadosh.service.impl;

import kado.kadosh.dto.CategoriaDTO;
import kado.kadosh.entities.Categoria;
import kado.kadosh.repository.CategoriaRepository;
import kado.kadosh.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepo;

    @Override
    public List<Categoria> listarActivas() {
        return categoriaRepo.findByActivoTrue();
    }

    @Override
    public List<Categoria> listarTodas() {
        return categoriaRepo.findAll();
    }

    @Override
    public Categoria buscarPorId(UUID id) {
        return categoriaRepo.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    @Override
    public Categoria guardar(CategoriaDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre de la categoría es obligatorio");
        }
        Categoria categoria = new Categoria();
        categoria.setCategoriaId(UUID.randomUUID());
        categoria.setNombre(dto.getNombre());
        categoria.setActivo(true);
        return categoriaRepo.save(categoria);
    }

    @Override
    public Categoria actualizar(UUID id, CategoriaDTO dto) {
        Categoria categoria = buscarPorId(id);
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre de la categoría es obligatorio");
        }
        categoria.setNombre(dto.getNombre());
        if (dto.getActivo() != null) {
            categoria.setActivo(dto.getActivo());
        }
        return categoriaRepo.save(categoria);
    }

    @Override
    public void eliminarLogico(UUID id) {
        Categoria cat = buscarPorId(id);
        cat.setActivo(false);
        categoriaRepo.save(cat);
    }
}
