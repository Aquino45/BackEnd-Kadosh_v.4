package kado.kadosh.repository;

import kado.kadosh.entities.Usuario;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    @Query("""
                SELECT u
                FROM Usuario u
                JOIN FETCH u.persona p
                WHERE p.dni = :dni AND u.estado = 'ACTIVO'
            """)
    Optional<Usuario> findActivoByPersonaDni(@Param("dni") String dni);

    @Query("""
                SELECT u
                FROM Usuario u
                JOIN FETCH u.persona p
                WHERE p.personaId = :personaId AND u.estado = 'ACTIVO'
            """)
    Optional<Usuario> findByPersonaIdActivo(@Param("personaId") UUID personaId);

    @Query("""
                SELECT u
                FROM Usuario u
                JOIN UsuarioRol ur ON ur.usuario.usuarioId = u.usuarioId
                JOIN Rol r ON r.rolId = ur.rol.rolId
                WHERE r.nombre = :nombreRol
                ORDER BY u.createdAt DESC
            """)
    List<Usuario> findByRolNombreOrderByCreatedAtDesc(@Param("nombreRol") String nombreRol);

    Optional<Usuario> findById(UUID id);


    @Query("""
                SELECT u
                FROM Usuario u
                JOIN FETCH u.persona p
                WHERE u.usuarioId = :usuarioId
            """)
    Optional<Usuario> findByIdWithPersona(@Param("usuarioId") UUID usuarioId);


    @Query("""
                SELECT u
                FROM Usuario u
                JOIN u.persona p
                JOIN UsuarioRol ur ON ur.usuario.usuarioId = u.usuarioId
                JOIN Rol r ON r.rolId = ur.rol.rolId
                WHERE u.estado = 'ACTIVO'
                  AND r.nombre = :nombreRol
                  AND (:dni IS NULL OR p.dni LIKE CONCAT(:dni, '%'))
                  AND (
                       :nombre IS NULL OR
                       LOWER(CONCAT(COALESCE(p.nombre,''), ' ', COALESCE(p.apellido,'')))
                       LIKE LOWER(CONCAT('%', :nombre, '%'))
                  )
                ORDER BY u.createdAt DESC
            """)
    List<Usuario> searchClientesFlexible(@Param("dni") String dni,
                                         @Param("nombre") String nombre,
                                         @Param("nombreRol") String nombreRol);

    @Query("""
                SELECT u
                FROM Usuario u
                JOIN FETCH u.persona p
                WHERE p.dni = :dni
            """)
    Optional<Usuario> findGeneralByPersonaDni(@Param("dni") String dni);

}
