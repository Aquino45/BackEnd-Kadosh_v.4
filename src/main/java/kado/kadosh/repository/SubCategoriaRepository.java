package kado.kadosh.repository;

import kado.kadosh.entities.SubCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SubCategoriaRepository extends JpaRepository<SubCategoria, UUID> {
    // El Front lo usa para decir: "Ya que eligió Monturas, muéstrame sus tipos (Carey, etc.)"
    List<SubCategoria> findByCategoria_CategoriaIdAndActivoTrue(UUID categoriaId);
}