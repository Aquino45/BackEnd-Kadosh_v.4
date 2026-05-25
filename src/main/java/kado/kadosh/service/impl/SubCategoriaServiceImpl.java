package kado.kadosh.service.impl;

import kado.kadosh.entities.SubCategoria; // ✅ Asegúrate que la entidad también sea SubCategoria
import kado.kadosh.repository.SubCategoriaRepository;
import kado.kadosh.service.SubCategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class SubCategoriaServiceImpl implements SubCategoriaService {

    @Autowired
    private SubCategoriaRepository subRepo;

    @Override
    public List<SubCategoria> listarPorCategoria(UUID categoriaId) {
        // El método del repositorio debe coincidir con el nombre del atributo en la entidad
        return subRepo.findByCategoria_CategoriaIdAndActivoTrue(categoriaId);
    }

    @Override
    public SubCategoria buscarPorId(UUID id) {
        return subRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada"));
    }

    @Override
    public SubCategoria guardar(SubCategoria subCategoria) {
        if (subCategoria.getSubCategoriaId() == null) {
            subCategoria.setSubCategoriaId(UUID.randomUUID());
        }
        return subRepo.save(subCategoria);
    }

    @Override
    public void eliminarLogico(UUID id) {
        SubCategoria sub = buscarPorId(id);
        sub.setActivo(false);
        subRepo.save(sub);
    }
}