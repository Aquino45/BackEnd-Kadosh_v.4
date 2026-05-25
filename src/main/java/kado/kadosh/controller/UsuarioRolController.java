
package kado.kadosh.controller;

import kado.kadosh.dto.UsuarioRolDTO;
import kado.kadosh.service.UsuarioRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuario-rol")
@RequiredArgsConstructor
public class UsuarioRolController {

    private final UsuarioRolService usuarioRolService;


    @PostMapping("/asignar")
    public ResponseEntity<?> asignar(@RequestBody UsuarioRolDTO dto) {
        if (dto.getUsuarioId() == null || dto.getRolId() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "usuarioId y rolId son obligatorios"
            ));
        }
        try {
            UsuarioRolDTO asignado = usuarioRolService.asignarRol(dto.getUsuarioId(), dto.getRolId());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Rol asignado correctamente",
                    "data", asignado
            ));
        } catch (Exception e) {

            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }


    @DeleteMapping("/quitar")
    public ResponseEntity<?> quitar(@RequestBody UsuarioRolDTO dto) {
        if (dto.getUsuarioId() == null || dto.getRolId() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "usuarioId y rolId son obligatorios"
            ));
        }
        boolean quitado = usuarioRolService.quitarRol(dto.getUsuarioId(), dto.getRolId());
        if (!quitado) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Relación usuario-rol no encontrada"
            ));
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Rol quitado correctamente"
        ));
    }


    @GetMapping("/listar/{usuarioId}")
    public ResponseEntity<?> listarRoles(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", usuarioRolService.listarRolesDeUsuario(usuarioId)
        ));
    }
}
