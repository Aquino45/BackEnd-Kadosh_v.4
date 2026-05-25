package kado.kadosh.controller;

import kado.kadosh.dto.ChangePasswordDTO;
import kado.kadosh.dto.PersonaUpdateDTO;
import kado.kadosh.dto.SubDatosUserDTO;
import kado.kadosh.scheduler.ActualizadorEdadesJob;
import kado.kadosh.service.UsuarioRolService;
import kado.kadosh.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kado.kadosh.dto.ClienteSearchRequest;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRolService usuarioRolService;
    private final ActualizadorEdadesJob actualizadorJob;

    // ✅ Listar solo usuarios con rol CLIENTE (de nuevos a antiguos)
    @GetMapping("/clientes")
    public ResponseEntity<?> listarClientes() {
        var data = usuarioService.listarClientesOrdenados();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "total", data.size(),
                "data", data
        ));
    }

    // ===========================================================
    // ✅ NUEVO: Obtener datos completos de un usuario por ID
    //    Esto sirve para el Modal de Información del Cliente
    // ===========================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable String id) {
        try {
            UUID uuid = parseFlexibleUUID(id);

            // Usamos el servicio que ya trae todo (Persona + Apoderado)
            Optional<SubDatosUserDTO> opt = usuarioService.subdatosPorUsuarioId(uuid);

            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Usuario no encontrado"));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", opt.get()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "ID inválido"));
        }
    }

    // ===========================================================
    // ✅ Actualizar datos de Persona de un Usuario específico
    // ===========================================================
    @PutMapping("/{id}/persona")
    public ResponseEntity<?> actualizarPersona(
            @PathVariable String id,
            @RequestBody PersonaUpdateDTO dto
    ) {
        UUID uuid = parseFlexibleUUID(id);

        // Ejecutamos la actualización (que ya incluye la lógica de apoderado en el Service)
        usuarioService.updatePersonaByUsuarioId(uuid, dto);

        // 🚀 CAMBIO: En lugar de devolver 'actualizada', pedimos los subdatos frescos
        // Esto asegura que el Front vea los cambios de la persona Y del apoderado.
        Optional<SubDatosUserDTO> subdatos = usuarioService.subdatosPorUsuarioId(uuid);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Datos actualizados correctamente.",
                "data", subdatos.orElseThrow()
        ));
    }

    @PostMapping("/clientes/search")
    public ResponseEntity<?> buscarClientes(@RequestBody ClienteSearchRequest body) {
        boolean sinFiltros = (body.getDni() == null || body.getDni().isBlank())
                && (body.getNombre() == null || body.getNombre().isBlank());
        if (sinFiltros) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Envía al menos uno de los filtros: 'dni' o 'nombre'."
            ));
        }

        var data = usuarioService.buscarClientes(body);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "total", data.size(),
                "data", data
        ));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String id) {
        try {

            UUID uuid = parseFlexibleUUID(id);

            usuarioService.eliminarUsuario(uuid);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of(
                            "success", true,
                            "message", "Usuario eliminado correctamente"
                    ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error al eliminar usuario: " + e.getMessage()
            ));
        }
    }

    // -----------------------
    // Util: parse flexible UUID
    // - acepta con guiones, sin guiones (32 hex), con prefijos "urn:uuid:" o "uuid:", o con llaves {}
    // - lanza IllegalArgumentException si no es válido
    // -----------------------
    private UUID parseFlexibleUUID(String raw) {
        if (raw == null) throw new IllegalArgumentException("ID de usuario nulo.");
        String s = raw.trim();

        // eliminar prefijos comunes
        if (s.toLowerCase().startsWith("urn:uuid:")) {
            s = s.substring(9);
        } else if (s.toLowerCase().startsWith("uuid:")) {
            s = s.substring(5);
        }

        // quitar llaves si existen
        if (s.startsWith("{") && s.endsWith("}")) {
            s = s.substring(1, s.length() - 1);
        }

        // Si ya tiene guiones, usar UUID.fromString directamente
        if (s.contains("-")) {
            return UUID.fromString(s);
        }

        // Si tiene 32 caracteres hex sin guiones, insertar guiones en las posiciones correctas
        if (s.length() == 32) {
            String dashed = s.substring(0, 8) + "-" +
                    s.substring(8, 12) + "-" +
                    s.substring(12, 16) + "-" +
                    s.substring(16, 20) + "-" +
                    s.substring(20);
            return UUID.fromString(dashed);
        }

        throw new IllegalArgumentException("Formato de UUID inválido: " + raw);
    }

    // 👇 2. Crea este endpoint temporal
    @GetMapping("/test-job")
    public String forzarActualizacion() {
        actualizadorJob.actualizarEdadesMasivas(); // <--- OBLIGAS AL ROBOT A TRABAJAR YA
        return "🤖 Robot ejecutado manualmente. Revisa la consola y la BD.";
    }

    // ── Roles ──────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/roles")
    public ResponseEntity<?> listarRoles(@PathVariable String id) {
        UUID uuid = parseFlexibleUUID(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", usuarioRolService.listarRolesDeUsuario(uuid)
        ));
    }

    @PostMapping("/{id}/rol")
    public ResponseEntity<?> asignarRol(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        try {
            String rolNombre = body.get("rolNombre");
            if (rolNombre == null || rolNombre.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El campo 'rolNombre' es obligatorio"));
            }
            UUID uuid = parseFlexibleUUID(id);
            var resultado = usuarioRolService.asignarRolPorNombre(uuid, rolNombre);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Rol '" + rolNombre + "' asignado correctamente",
                    "data", resultado
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/cambiar-password/{usuarioId}")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable UUID usuarioId,
            @RequestBody ChangePasswordDTO dto) {
        try {
            usuarioService.cambiarPassword(usuarioId, dto.getActualPassword(), dto.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada con éxito"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/reset-password/{usuarioId}")
    public ResponseEntity<?> resetPasswordAdmin(
            @PathVariable UUID usuarioId,
            @RequestBody Map<String, String> body) {

        try {
            String nuevaClave = body.get("newPassword");

            if (nuevaClave == null || nuevaClave.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La nueva contraseña es obligatoria"));
            }

            usuarioService.resetearPasswordDirecto(usuarioId, nuevaClave);
            return ResponseEntity.ok(Map.of("message", "Contraseña reseteada con éxito por el administrador"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}