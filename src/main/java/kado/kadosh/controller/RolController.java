package kado.kadosh.controller;

import kado.kadosh.dto.RolDTO;
import kado.kadosh.entities.Rol;
import kado.kadosh.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    // ===================== LISTAR TODOS =====================
    @GetMapping
    public ResponseEntity<?> listar() {
        List<RolDTO> data = rolService.listar()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    // ===================== LISTAR ACTIVOS =====================
    @GetMapping("/activos")
    public ResponseEntity<?> listarActivos() {
        List<RolDTO> data = rolService.listarActivos()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    // ===================== CREAR NUEVO ROL =====================
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody RolDTO dto) {
        Rol rol = toEntity(dto);
        Rol nuevo = rolService.save(rol);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Rol creado correctamente",
                "data", toDto(nuevo)
        ));
    }

    // ===================== ELIMINAR ROL =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable UUID id) {
        try {
            rolService.deleteById(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Rol eliminado correctamente"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ===================== CONVERTIDORES =====================
    private RolDTO toDto(Rol e) {
        if (e == null) return null;
        return new RolDTO(
                e.getRolId(),
                e.getNombre(),
                e.getDescripcion(),
                e.getActivo(),
                e.getCreatedAt()
        );
    }

    private Rol toEntity(RolDTO dto) {
        Rol r = new Rol();
        r.setRolId(dto.getRolId());
        r.setNombre(dto.getNombre());
        r.setDescripcion(dto.getDescripcion());
        r.setActivo(dto.getActivo());
        return r;
    }
}
