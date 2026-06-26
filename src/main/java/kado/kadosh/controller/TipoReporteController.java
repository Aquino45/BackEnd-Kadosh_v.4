package kado.kadosh.controller;

import kado.kadosh.dto.TipoReporteDTO;
import kado.kadosh.entities.TipoReporte;
import kado.kadosh.service.TipoReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tipo-reportes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('Admin','Cajero')")
public class TipoReporteController {

    private final TipoReporteService tipoReporteService;

    // ===================== LISTAR TODOS =====================
    @GetMapping
    public ResponseEntity<?> listar() {
        List<TipoReporteDTO> data = tipoReporteService.listar()
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
        List<TipoReporteDTO> data = tipoReporteService.listarActivos()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    // ===================== BUSCAR POR ID =====================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        try {
            TipoReporte tipoReporte = tipoReporteService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de reporte no encontrado"));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", toDto(tipoReporte)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ===================== CREAR NUEVO TIPO REPORTE =====================
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> crear(@RequestBody TipoReporteDTO dto) {
        try {
            TipoReporte tipoReporte = toEntity(dto);
            TipoReporte nuevo = tipoReporteService.save(tipoReporte);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tipo de reporte creado correctamente",
                    "data", toDto(nuevo)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ===================== ACTUALIZAR TIPO REPORTE =====================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @RequestBody TipoReporteDTO dto) {
        try {
            TipoReporte existente = tipoReporteService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de reporte no encontrado"));

            existente.setCodigo(dto.getCodigo());
            existente.setNombre(dto.getNombre());
            existente.setDescripcion(dto.getDescripcion());
            existente.setPermitePdf(dto.getPermitePdf());
            existente.setPermiteExcel(dto.getPermiteExcel());
            existente.setActivo(dto.getActivo());

            TipoReporte actualizado = tipoReporteService.save(existente);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tipo de reporte actualizado correctamente",
                    "data", toDto(actualizado)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ===================== ELIMINAR FÍSICO =====================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> eliminar(@PathVariable UUID id) {
        try {
            tipoReporteService.deleteById(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tipo de reporte eliminado correctamente"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ===================== DESACTIVAR LÓGICO =====================
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> desactivar(@PathVariable UUID id) {
        try {
            TipoReporte desactivado = tipoReporteService.desactivar(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tipo de reporte desactivado correctamente",
                    "data", toDto(desactivado)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ===================== CONVERTIDORES =====================
    private TipoReporteDTO toDto(TipoReporte e) {
        if (e == null) return null;

        return new TipoReporteDTO(
                e.getTipoReporteId(),
                e.getCodigo(),
                e.getNombre(),
                e.getDescripcion(),
                e.getPermitePdf(),
                e.getPermiteExcel(),
                e.getActivo(),
                e.getCreatedAt()
        );
    }

    private TipoReporte toEntity(TipoReporteDTO dto) {
        TipoReporte t = new TipoReporte();
        t.setTipoReporteId(dto.getTipoReporteId() != null ? dto.getTipoReporteId() : UUID.randomUUID());
        t.setCodigo(dto.getCodigo());
        t.setNombre(dto.getNombre());
        t.setDescripcion(dto.getDescripcion());
        t.setPermitePdf(dto.getPermitePdf());
        t.setPermiteExcel(dto.getPermiteExcel());
        t.setActivo(dto.getActivo());
        return t;
    }
}
