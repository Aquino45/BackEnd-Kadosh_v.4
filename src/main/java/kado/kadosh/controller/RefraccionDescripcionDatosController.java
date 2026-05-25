package kado.kadosh.controller;

import kado.kadosh.dto.RefraccionDescripcionDatosDTO;
import kado.kadosh.service.RefraccionDescripcionDatosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/refraccion-descripcion-datos")
@RequiredArgsConstructor
public class RefraccionDescripcionDatosController {

    private final RefraccionDescripcionDatosService service;

    @GetMapping
    public ResponseEntity<List<RefraccionDescripcionDatosDTO>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefraccionDescripcionDatosDTO> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<RefraccionDescripcionDatosDTO> create(
            @RequestBody RefraccionDescripcionDatosDTO dto
    ) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RefraccionDescripcionDatosDTO> update(
            @PathVariable UUID id,
            @RequestBody RefraccionDescripcionDatosDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
