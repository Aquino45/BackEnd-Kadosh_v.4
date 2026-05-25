package kado.kadosh.repository;

import kado.kadosh.entities.RefraccionDescripcionDatos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefraccionDescripcionDatosRepository
        extends JpaRepository<RefraccionDescripcionDatos, UUID> {


}
