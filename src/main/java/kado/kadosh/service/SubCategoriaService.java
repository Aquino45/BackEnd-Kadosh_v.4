package kado.kadosh.service;

import kado.kadosh.entities.SubCategoria;
import java.util.List;
import java.util.UUID;

public interface SubCategoriaService {
    // Para que al elegir "Monturas", el Front pida sus hijos (Carey, Metal, etc.)
    List<SubCategoria> listarPorCategoria(UUID categoriaId);
    SubCategoria buscarPorId(UUID id);
    SubCategoria guardar(SubCategoria subcategoria);
    void eliminarLogico(UUID id);
}