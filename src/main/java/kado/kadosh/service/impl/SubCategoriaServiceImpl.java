package kado.kadosh.service.impl;

import kado.kadosh.dto.SubCategoriaDTO;
import kado.kadosh.entities.Categoria;
import kado.kadosh.entities.SubCategoria;
import kado.kadosh.repository.CategoriaRepository;
import kado.kadosh.repository.SubCategoriaRepository;
import kado.kadosh.service.SubCategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubCategoriaServiceImpl implements SubCategoriaService {

    private final SubCategoriaRepository subRepo;
    private final CategoriaRepository categoriaRepo;

    @Override
    public List<SubCategoria> listarPorCategoria(UUID categoriaId) {
        return subRepo.findByCategoria_CategoriaIdAndActivoTrue(categoriaId);
    }

    @Override
    public List<SubCategoria> listarTodas() {
        return subRepo.findAll();
    }

    @Override
    public SubCategoria buscarPorId(UUID id) {
        return subRepo.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada"));
    }

    @Override
    public SubCategoria guardar(SubCategoriaDTO dto) {
        SubCategoria subCategoria = new SubCategoria();
        subCategoria.setSubCategoriaId(UUID.randomUUID());
        subCategoria.setActivo(true);
        aplicarDTO(subCategoria, dto);
        return subRepo.save(subCategoria);
    }

    @Override
    public SubCategoria actualizar(UUID id, SubCategoriaDTO dto) {
        SubCategoria subCategoria = buscarPorId(id);
        aplicarDTO(subCategoria, dto);
        if (dto.getActivo() != null) {
            subCategoria.setActivo(dto.getActivo());
        }
        return subRepo.save(Objects.requireNonNull(subCategoria));
    }

    @Override
    public void eliminarLogico(UUID id) {
        SubCategoria sub = buscarPorId(id);
        sub.setActivo(false);
        subRepo.save(sub);
    }

    private void aplicarDTO(SubCategoria subCategoria, SubCategoriaDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre de la subcategoría es obligatorio");
        }
        if (dto.getCategoriaId() == null) {
            throw new RuntimeException("La subcategoría debe pertenecer a una categoría");
        }
        Categoria categoria = categoriaRepo.findById(Objects.requireNonNull(dto.getCategoriaId()))
                .orElseThrow(() -> new RuntimeException("La categoría enviada no existe"));

        subCategoria.setNombre(dto.getNombre());
        subCategoria.setCategoria(categoria);
    }
}
