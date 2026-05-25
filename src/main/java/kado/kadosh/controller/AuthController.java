package kado.kadosh.controller;

import kado.kadosh.config.JwtTokenProvider;
import kado.kadosh.dto.LoginRequestDTO;
import kado.kadosh.dto.RegisterRequestDTO;
import kado.kadosh.dto.SubDatosUserDTO;
import kado.kadosh.entities.Persona;
import kado.kadosh.entities.Usuario;
import kado.kadosh.service.PersonaService;
import kado.kadosh.service.UsuarioRolService;
import kado.kadosh.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import kado.kadosh.enums.EstadoUsuario;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PersonaService personaService;
    private final UsuarioService usuarioService;
    private final UsuarioRolService usuarioRolService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO req) {

        // 1. Buscamos al usuario (Asegúrate de usar el buscador general que no filtra por 'ACTIVO')
        Optional<Usuario> opt = usuarioService.findByDni(req.getDni());

        if (opt.isEmpty()) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "Credenciales inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        Usuario u = opt.get();

        // 🚨 VALIDACIÓN DE ESTADOS (Baneado o Eliminado)
        // Entrará aquí porque el buscador general sí encuentra al usuario aunque no esté ACTIVO
        if (u.getEstado() == EstadoUsuario.LOBANANEE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "Cuenta suspendida. Contacte al administrador."));
        }

        if (u.getEstado() == EstadoUsuario.SEFUE) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "Dejaste la óptica por mucho tiempo, para volver a activar tu cuenta contactate con el administrador");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        // 2. Validación de contraseña
        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "Credenciales inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        // 3. Generación del Token JWT
        String token = jwtTokenProvider.generateToken(u.getUsuarioId());

        // 4. Datos de la Persona vinculada
        Persona p = u.getPersona();
        String nombre    = (p != null) ? p.getNombre()    : null;
        String apellido  = (p != null) ? p.getApellido()  : null;
        String dni       = (p != null) ? p.getDni()       : null;
        String email     = (p != null) ? p.getEmail()     : null;
        String telefono  = (p != null) ? p.getTelefono()  : null;
        String imagenUrl = (p != null) ? p.getImagenUrl() : null;

        String rol = usuarioRolService.rolNombrePorUsuarioId(u.getUsuarioId()).orElse(null);

        // 5. Construcción del objeto de usuario para el Frontend
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", u.getUsuarioId() != null ? u.getUsuarioId().toString() : null);
        user.put("nombre", nombre);
        user.put("apellido", apellido);
        user.put("dni", dni);
        user.put("email", email);
        user.put("telefono", telefono);

        // Enviamos el 'estado' como texto (ACTIVO, SEFUE, etc.)
        user.put("estado", u.getEstado().toString());
        user.put("rol", rol);
        user.put("imagen_url", imagenUrl);

        // 6. Información del Apoderado (Si existe)
        if (u.getApoderado() != null) {
            Map<String, Object> apo = new LinkedHashMap<>();
            apo.put("nombre", u.getApoderado().getNombre());
            apo.put("apellido", u.getApoderado().getApellido());
            apo.put("dni", u.getApoderado().getDni());
            apo.put("telefono", u.getApoderado().getTelefono());
            apo.put("parentesco", u.getApoderado().getParentesco());
            user.put("apoderado", apo);
        } else {
            user.put("apoderado", null);
        }

        // 7. Respuesta final exitosa
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("token", token);
        body.put("user", user);

        return ResponseEntity.ok(body);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO req) {

        // 1. Validación de existencia
        if (personaService.existsByDni(req.getDni())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "El DNI ya está registrado"));
        }

        // 2. Validación de imagen
        String imagenUrl = req.getImagenUrl();
        if (imagenUrl != null && imagenUrl.length() > 300) {
            imagenUrl = null;
        }
        if (imagenUrl == null || imagenUrl.isBlank()) {
            String inicial = (req.getNombre() != null && !req.getNombre().isBlank())
                    ? req.getNombre().trim().substring(0, 1).toUpperCase()
                    : "U";
            imagenUrl = "https://ui-avatars.com/api/?name=" + inicial
                    + "&background=891aab&color=ffffff&size=128&bold=true";
        }

        // 3. Crear Persona (SIN GUARDARLA AÚN)
        Persona persona = new Persona();
        persona.setPersonaId(UUID.randomUUID());
        persona.setNombre(req.getNombre());
        persona.setApellido(req.getApellido());
        persona.setEmail(req.getEmail());
        persona.setTelefono(req.getTelefono());
        persona.setDni(req.getDni());
        persona.setImagenUrl(imagenUrl);
        persona.setActivo(true);

        if (req.getFechaNacimiento() != null) {
            persona.setFechaNacimiento(req.getFechaNacimiento());
            persona.setEdad(calcularEdad(req.getFechaNacimiento()));
        } else if (req.getEdad() != null) {
            persona.setEdad(req.getEdad());
        }

        // ❌ QUITAR: personaService.save(persona);
        // Esto es lo que causaba el error de Optimistic Locking.

        // 4. Crear Usuario
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(UUID.randomUUID());
        usuario.setPassword(passwordEncoder.encode(req.getPassword()));
        usuario.setPersona(persona);

        // 🚨 CAMBIO 3: BORRA O COMENTA ESTA LÍNEA
        // usuario.setActivo(true);  <-- BORRAR ESTO (Ya nace ACTIVO por defecto en la entidad)

        // 5. Gestión del Apoderado
        if (req.getApoderado() != null && req.getApoderado().getNombre() != null) {
            var apoDto = req.getApoderado();

            kado.kadosh.entities.Apoderado apoderado = new kado.kadosh.entities.Apoderado();
            apoderado.setApoderadoId(UUID.randomUUID());
            apoderado.setNombre(apoDto.getNombre());
            apoderado.setApellido(apoDto.getApellido());
            apoderado.setDni(apoDto.getDni());
            apoderado.setTelefono(apoDto.getTelefono());
            apoderado.setParentesco(apoDto.getParentesco());

            usuario.setApoderado(apoderado);
        }

        // 6. GUARDADO ÚNICO
        // Gracias a CascadeType.ALL, esto guarda Usuario, Persona y Apoderado
        // de forma atómica en una sola operación de base de datos.
        usuarioService.save(usuario);

        // 7. Asignación de Rol
        usuarioRolService.asignarRolPorNombre(usuario.getUsuarioId(), "Cliente");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "message", "Usuario registrado con éxito"));
    }


    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Falta token"));
        }

        String token = authorization.substring(7);

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Token inválido"));
        }

        String usuarioIdStr = jwtTokenProvider.getUsuarioIdFromToken(token);
        if (usuarioIdStr == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Token sin usuarioId"));
        }

        UUID usuarioId = UUID.fromString(usuarioIdStr);

        Optional<SubDatosUserDTO> dtoOpt = usuarioService.subdatosPorUsuarioId(usuarioId);
        if (dtoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Usuario no encontrado"));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", dtoOpt.get()
        ));
    }


    private Integer calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return null;
        LocalDate hoy = LocalDate.now();
        if (fechaNacimiento.isAfter(hoy)) return 0;
        return Period.between(fechaNacimiento, hoy).getYears();
    }

    @SuppressWarnings("unused")
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
