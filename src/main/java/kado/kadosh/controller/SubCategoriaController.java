package kado.kadosh.controller;

import kado.kadosh.dto.SubCategoriaDTO;
import kado.kadosh.entities.SubCategoria;
import kado.kadosh.service.SubCategoriaService;
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
@RequestMapping("/api/subcategorias")
@RequiredArgsConstructor
public class SubCategoriaController {

    private final SubCategoriaService subService;

    @GetMapping("/categoria/{categoriaId}")
    public List<SubCategoriaDTO> listarPorPadre(@PathVariable UUID categoriaId) {
        return subService.listarPorCategoria(categoriaId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    public List<SubCategoriaDTO> listarTodas() {
        return subService.listarTodas().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SubCategoriaDTO buscarPorId(@PathVariable UUID id) {
        return toDTO(subService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<SubCategoriaDTO> guardar(@RequestBody SubCategoriaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(subService.guardar(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public SubCategoriaDTO actualizar(@PathVariable UUID id, @RequestBody SubCategoriaDTO dto) {
        return toDTO(subService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable UUID id) {
        subService.eliminarLogico(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Subcategoría eliminada correctamente",
                "idEliminado", id.toString()
        ));
    }

    private SubCategoriaDTO toDTO(SubCategoria s) {
        SubCategoriaDTO dto = new SubCategoriaDTO();
        dto.setSubCategoriaId(s.getSubCategoriaId());
        dto.setNombre(s.getNombre());
        dto.setActivo(s.getActivo());
        if (s.getCategoria() != null) {
            dto.setCategoriaId(s.getCategoria().getCategoriaId());
            dto.setNombreCategoria(s.getCategoria().getNombre());
        }
        return dto;
    }
}
