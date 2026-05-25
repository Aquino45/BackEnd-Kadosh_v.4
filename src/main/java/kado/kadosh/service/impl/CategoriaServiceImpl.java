package kado.kadosh.service.impl;

import kado.kadosh.entities.Categoria;
import kado.kadosh.repository.CategoriaRepository;
import kado.kadosh.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired private CategoriaRepository categoriaRepo;

    @Override
    public List<Categoria> listarActivas() {
        return categoriaRepo.findByActivoTrue();
    }

    @Override
    public Categoria buscarPorId(UUID id) {
        return categoriaRepo.findById(id).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        if (categoria.getCategoriaId() == null) categoria.setCategoriaId(UUID.randomUUID());
        return categoriaRepo.save(categoria);
    }

    @Override
    public void eliminarLogico(UUID id) {
        Categoria cat = buscarPorId(id);
        cat.setActivo(false);
        categoriaRepo.save(cat);
    }
}