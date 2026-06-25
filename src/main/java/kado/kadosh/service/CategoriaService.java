package kado.kadosh.service;

import kado.kadosh.dto.CategoriaDTO;
import kado.kadosh.entities.Categoria;
import java.util.List;
import java.util.UUID;

public interface CategoriaService {
    List<Categoria> listarActivas(); // Para mostrar "Lunas", "Monturas"
    List<Categoria> listarTodas();
    Categoria buscarPorId(UUID id);
    Categoria guardar(CategoriaDTO dto);
    Categoria actualizar(UUID id, CategoriaDTO dto);
    void eliminarLogico(UUID id); // Cambia activo a false
}
