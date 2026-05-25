package kado.kadosh.service.impl;

import kado.kadosh.dto.ClienteListItemDTO;
import kado.kadosh.dto.SubDatosUserDTO;
import kado.kadosh.dto.PersonaUpdateDTO;
import kado.kadosh.entities.Apoderado;
import kado.kadosh.entities.Persona;
import kado.kadosh.entities.Usuario;
import kado.kadosh.repository.PersonaRepository;
import kado.kadosh.repository.UsuarioRepository;
import kado.kadosh.repository.UsuarioRolRepository;
import kado.kadosh.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kado.kadosh.dto.ClienteSearchRequest;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import kado.kadosh.enums.EstadoUsuario; // <--- Agrega esto
import org.springframework.security.crypto.password.PasswordEncoder; // Necesitas esta dependencia

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder; // 🚀 INYECTA ESTO

    // 🚀 AÑADE ESTO:
    private final jakarta.persistence.EntityManager entityManager;

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        // Merge es el comando correcto cuando manejas IDs manuales (UUID)
        // porque decide si hacer INSERT o UPDATE basándose en si el ID existe en la DB.
        return entityManager.merge(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByDni(String dni) {
        // ✅ Cambiamos el "Activo" por el "General"
        return usuarioRepository.findGeneralByPersonaDni(dni);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByPersonaIdActivo(UUID personaId) {
        return usuarioRepository.findByPersonaIdActivo(personaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> rolUnicoPorUsuarioId(UUID usuarioId) {
        return usuarioRolRepository.findByUsuario_UsuarioId(usuarioId).stream()
                .filter(Objects::nonNull)
                .map(ur -> ur.getRol() != null ? ur.getRol().getNombre() : null)
                .filter(r -> r != null && !r.isBlank())
                .findFirst();
    }

    // Borra la inyección de HistorialOpticoService para eliminar el ciclo

    @Override
    @Transactional(readOnly = true)
    public Optional<SubDatosUserDTO> subdatosPorUsuarioId(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId).map(u -> {
            var persona = u.getPersona();
            var apoderado = u.getApoderado();

            // ... (Toda la lógica de variables sigue igual) ...
            String dni        = (persona != null) ? persona.getDni()       : "---";
            String nombre     = (persona != null) ? persona.getNombre()    : null;
            String apellido   = (persona != null) ? persona.getApellido()  : null;
            String correo     = (persona != null) ? persona.getEmail()     : null;
            String telefono   = (persona != null) ? persona.getTelefono()  : null;
            String imagenUrl  = (persona != null) ? persona.getImagenUrl() : null;
            LocalDate fechaNac = (persona != null) ? persona.getFechaNacimiento() : null;

            Integer edadCalculada = calcularEdad(fechaNac);

            String rol = rolUnicoPorUsuarioId(usuarioId).orElse("Cliente");

            // 🚨 AQUÍ ESTÁ EL CAMBIO EN EL RETURN:
            return new SubDatosUserDTO(
                    // 1. Datos cuenta
                    u.getUsuarioId(),
                    rol,
                    u.getCreatedAt(),

                    u.getEstado().toString(), // 🚨 AGREGA ESTA LÍNEA (El estado del usuario)

                    // 2. Datos Persona
                    nombre,
                    apellido,
                    dni,
                    correo,
                    telefono,
                    imagenUrl,
                    fechaNac,
                    edadCalculada,

                    // 3. Datos Apoderado
                    (apoderado != null) ? apoderado.getNombre() : "Ninguno",
                    (apoderado != null) ? apoderado.getApellido() : "",
                    (apoderado != null) ? apoderado.getDni() : "",
                    (apoderado != null) ? apoderado.getTelefono() : "",
                    (apoderado != null) ? apoderado.getParentesco() : ""
            );
        });
    }

    // ===========================================================
    // ✅ Actualizar datos de Persona vía usuarioId
    //     - Si viene fechaNacimiento → recalculamos edad
    //     - Si viene eliminarApoderado=true → borramos al apoderado
    // ===========================================================
    @Override
    @Transactional
    public Persona updatePersonaByUsuarioId(UUID usuarioId, PersonaUpdateDTO dto) {
        // 1. Buscamos al usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        Persona p = usuario.getPersona();
        if (p == null) {
            throw new IllegalStateException("El usuario no tiene persona asociada: " + usuarioId);
        }

        // --- A. ACTUALIZACIÓN DE DATOS DE LA PERSONA (IGUAL QUE ANTES) ---
        if (dto.getNombre()    != null) p.setNombre(dto.getNombre().trim());
        if (dto.getApellido()  != null) p.setApellido(dto.getApellido().trim());
        if (dto.getEmail()     != null) p.setEmail(dto.getEmail().trim());
        if (dto.getTelefono()  != null) p.setTelefono(dto.getTelefono().trim());
        if (dto.getImagenUrl() != null) p.setImagenUrl(dto.getImagenUrl().trim());
        if (dto.getDni()       != null) p.setDni(dto.getDni().trim());

        if (dto.getFechaNacimiento() != null) {
            p.setFechaNacimiento(dto.getFechaNacimiento());
            p.setEdad(calcularEdad(p.getFechaNacimiento()));
        } else if (dto.getEdad() != null) {
            p.setEdad(dto.getEdad());
        }

        // 🚨 AQUÍ AGREGAMOS LA ACTUALIZACIÓN DEL ESTADO DEL USUARIO 🚨
        // Esto verifica si enviaste un estado nuevo (ACTIVO, SEFUE, LOBANANEE)
        if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
            try {
                // Convertimos el String del DTO al Enum
                EstadoUsuario nuevoEstado = EstadoUsuario.valueOf(dto.getEstado().toUpperCase());
                usuario.setEstado(nuevoEstado);
            } catch (IllegalArgumentException e) {
                // Si mandan cualquier cosa que no sea un estado válido
                throw new IllegalArgumentException("Estado inválido: " + dto.getEstado());
            }
        }
        // 🚨 FIN DEL AGREGADO 🚨

        // --- B. LÓGICA DEL APODERADO (IGUAL QUE ANTES) ---

        // 1. ¿El usuario pidió BORRAR al apoderado?
        if (Boolean.TRUE.equals(dto.getEliminarApoderado())) {
            // Al ponerlo en null, si tienes orphanRemoval=true en la entidad Usuario,
            // se borrará de la base de datos automáticamente.
            usuario.setApoderado(null);
        }
        // 2. ¿El usuario envió datos para CREAR o ACTUALIZAR?
        else if (dto.getNombreApoderado() != null && !dto.getNombreApoderado().isBlank()) {

            Apoderado ap = usuario.getApoderado();

            if (ap == null) {
                // Si no existía, lo creamos
                ap = new Apoderado();
                ap.setApoderadoId(UUID.randomUUID());
                usuario.setApoderado(ap);
            }

            // Actualizamos los campos
            ap.setNombre(dto.getNombreApoderado().trim());

            if (dto.getApellidoApoderado() != null) ap.setApellido(dto.getApellidoApoderado().trim());
            if (dto.getDniApoderado()      != null) ap.setDni(dto.getDniApoderado().trim());
            if (dto.getTelefonoApoderado() != null) ap.setTelefono(dto.getTelefonoApoderado().trim());
            if (dto.getParentesco()        != null) ap.setParentesco(dto.getParentesco().trim());
        }

        // --- PERSISTENCIA ---
        // Guardamos 'usuario' y por cascada se guarda 'persona', 'apoderado' y el nuevo 'estado'
        this.save(usuario);

        return p;
    }

    // ===========================================================
    // Listar clientes por rol y fecha DESC
    // ===========================================================
    @Override
    @Transactional(readOnly = true)
    public List<ClienteListItemDTO> listarClientesOrdenados() {
        List<Usuario> usuarios = usuarioRepository.findByRolNombreOrderByCreatedAtDesc("Cliente");

        return usuarios.stream().map(u -> {
            Persona p = u.getPersona();
            return new ClienteListItemDTO(
                    u.getUsuarioId(),
                    p != null ? p.getNombre() : null,
                    p != null ? p.getApellido() : null,
                    p != null ? p.getEdad() : null,
                    p != null ? p.getEmail() : null,
                    p != null ? p.getTelefono() : null,
                    p != null ? p.getDni() : null,
                    p != null ? p.getImagenUrl() : null,
                    p != null ? p.getFechaNacimiento() : null,
                    u.getCreatedAt()
            );
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteListItemDTO> buscarClientes(ClienteSearchRequest req) {
        String dni = (req.getDni() == null || req.getDni().isBlank()) ? null : req.getDni().trim();
        String nombre = (req.getNombre() == null || req.getNombre().isBlank()) ? null : req.getNombre().trim();

        if (dni == null && nombre == null) {
            return List.of();
        }

        List<Usuario> usuarios = usuarioRepository.searchClientesFlexible(dni, nombre, "Cliente");

        return usuarios.stream().map(u -> {
            Persona p = u.getPersona();
            return new ClienteListItemDTO(
                    u.getUsuarioId(),
                    p != null ? p.getNombre() : null,
                    p != null ? p.getApellido() : null,
                    p != null ? p.getEdad() : null,
                    p != null ? p.getEmail() : null,
                    p != null ? p.getTelefono() : null,
                    p != null ? p.getDni() : null,
                    p != null ? p.getImagenUrl() : null,
                    p != null ? p.getFechaNacimiento() : null,
                    u.getCreatedAt()
            );
        }).toList();
    }

    @Override
    @Transactional
    public void eliminarUsuario(UUID usuarioId) {
        // 1. Buscamos al usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con el ID proporcionado: " + usuarioId));

        // 2. Eliminamos los roles manualmente
        // Seguimos haciéndolo así porque UsuarioRol suele ser una tabla intermedia sin CascadeType.REMOVE
        usuarioRolRepository.deleteByUsuarioUsuarioId(usuarioId);

        // 3. 🚀 ELIMINACIÓN TOTAL
        // Usamos el EntityManager directamente para asegurar que se remueva el objeto
        // junto con su Persona y su Apoderado de forma atómica.
        if (entityManager.contains(usuario)) {
            entityManager.remove(usuario);
        } else {
            entityManager.remove(entityManager.merge(usuario));
        }
    }


    // PARA DESPUESsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss:
//    @Override
//    @Transactional
//    public void eliminarUsuario(UUID usuarioId) {
//        // 1. Buscamos al usuario
//        Usuario usuario = usuarioRepository.findById(usuarioId)
//                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));
//
//        // 2. CAMBIO CLAVE: Ya no borramos físicamente.
//        // Cambiamos el estado a SEFUE para que Oracle no se queje y guardamos historial.
//        usuario.setEstado(EstadoUsuario.SEFUE);
//
//        usuarioRepository.save(usuario);
//    }



    // ==========================
    // Helper para edad
    // ==========================
    private Integer calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return null;
        LocalDate hoy = LocalDate.now();
        if (fechaNacimiento.isAfter(hoy)) {
            return 0;
        }
        return Period.between(fechaNacimiento, hoy).getYears();
    }


    @Override
    @Transactional
    public void cambiarPassword(UUID usuarioId, String actualPassword, String newPassword) {
        // 1. Buscar al usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 2. Verificar que la contraseña actual coincida (encriptada vs texto plano)
        if (!passwordEncoder.matches(actualPassword, usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // 3. Validar que la nueva no sea igual a la anterior (opcional pero recomendado)
        if (passwordEncoder.matches(newPassword, usuario.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior");
        }

        // 4. Encriptar la nueva contraseña y guardar
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void resetearPasswordDirecto(UUID usuarioId, String newPassword) {
        // 1. Buscamos al usuario por su ID
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 2. Encriptamos la nueva contraseña directamente
        usuario.setPassword(passwordEncoder.encode(newPassword));

        // 3. Persistimos el cambio
        usuarioRepository.save(usuario);
    }
}
