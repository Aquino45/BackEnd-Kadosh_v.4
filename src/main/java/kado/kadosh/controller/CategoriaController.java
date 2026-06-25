package kado.kadosh.controller;

import kado.kadosh.dto.CategoriaDTO;
import kado.kadosh.entities.Categoria;
import kado.kadosh.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping("/activas")
    public List<CategoriaDTO> listarActivas() {
        return categoriaService.listarActivas().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    public List<CategoriaDTO> listarTodas() {
        return categoriaService.listarTodas().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoriaDTO buscarPorId(@PathVariable UUID id) {
        return toDTO(categoriaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<CategoriaDTO> guardar(@RequestBody CategoriaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(categoriaService.guardar(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public CategoriaDTO actualizar(@PathVariable UUID id, @RequestBody CategoriaDTO dto) {
        return toDTO(categoriaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable UUID id) {
        categoriaService.eliminarLogico(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Categoría eliminada correctamente",
                "idEliminado", id.toString()
        ));
    }

    private CategoriaDTO toDTO(Categoria c) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setCategoriaId(c.getCategoriaId());
        dto.setNombre(c.getNombre());
        dto.setActivo(c.getActivo());
        return dto;
    }
}
