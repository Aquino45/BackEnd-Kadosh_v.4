package kado.kadosh.repository;

import kado.kadosh.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    // Para listar "Monturas", "Lunas", etc., que estén activas
    List<Categoria> findByActivoTrue();
}