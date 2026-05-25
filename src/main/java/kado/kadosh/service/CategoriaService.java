package kado.kadosh.service;

import kado.kadosh.entities.Categoria;
import java.util.List;
import java.util.UUID;

public interface CategoriaService {
    List<Categoria> listarActivas(); // Para mostrar "Lunas", "Monturas"
    Categoria buscarPorId(UUID id);
    Categoria guardar(Categoria categoria);
    void eliminarLogico(UUID id); // Cambia activo a false
}