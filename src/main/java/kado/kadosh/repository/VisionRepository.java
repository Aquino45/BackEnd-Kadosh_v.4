package kado.kadosh.repository;

import kado.kadosh.entities.Vision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VisionRepository extends JpaRepository<Vision, UUID> {

}
