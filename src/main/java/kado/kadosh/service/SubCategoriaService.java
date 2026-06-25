package kado.kadosh.service;

import kado.kadosh.dto.SubCategoriaDTO;
import kado.kadosh.entities.SubCategoria;
import java.util.List;
import java.util.UUID;

public interface SubCategoriaService {
    // Para que al elegir "Monturas", el Front pida sus hijos (Carey, Metal, etc.)
    List<SubCategoria> listarPorCategoria(UUID categoriaId);
    List<SubCategoria> listarTodas();
    SubCategoria buscarPorId(UUID id);
    SubCategoria guardar(SubCategoriaDTO dto);
    SubCategoria actualizar(UUID id, SubCategoriaDTO dto);
    void eliminarLogico(UUID id);
}
