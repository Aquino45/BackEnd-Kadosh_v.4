package kado.kadosh.controller;

import kado.kadosh.dto.HistorialOpticoDTO;
import kado.kadosh.dto.SubDatosUserDTO;
import kado.kadosh.dto.VisionDTO;
import kado.kadosh.entities.HistorialOptico;
import kado.kadosh.entities.Usuario;
import kado.kadosh.entities.Vision;
import kado.kadosh.service.HistorialOpticoService;
import kado.kadosh.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/historial-optico")
public class HistorialOpticoController {

    @Autowired
    private HistorialOpticoService historialService;

    @Autowired
    private UsuarioService usuarioService;

    // ========================
    // 🟢 CREAR (POST)
    // ========================
    @PostMapping
    public ResponseEntity<HistorialOpticoDTO> crearHistorial(@RequestBody HistorialOpticoDTO dto) {
        HistorialOptico historial = historialService.crearHistorial(dto);
        HistorialOpticoDTO resp = toDTO(historial);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // ========================
    // 🟡 EDITAR (PUT)
    // ========================
    @PutMapping("/{id}")
    public ResponseEntity<HistorialOpticoDTO> actualizarHistorial(
            @PathVariable String id,
            @RequestBody HistorialOpticoDTO dto) {

        UUID uuid = UUID.fromString(id);
        HistorialOptico actualizado = historialService.actualizarHistorial(uuid, dto);
        HistorialOpticoDTO resp = toDTO(actualizado);
        return ResponseEntity.ok(resp);
    }

    // ========================
    // 🔁 MAPPER ENTITY → DTO
    // ========================
    private HistorialOpticoDTO toDTO(HistorialOptico historial) {
        if (historial == null) return null;

        HistorialOpticoDTO dto = new HistorialOpticoDTO();
        dto.setHistorialOpticoId(historial.getHistorialOpticoId());

        // ✅ SALTO DE DATOS: Historial -> Usuario -> Persona (PACIENTE)
        if (historial.getUsuario() != null) {
            dto.setUsuarioId(historial.getUsuario().getUsuarioId());

            if (historial.getUsuario().getPersona() != null) {
                var p = historial.getUsuario().getPersona();
                dto.setPaciente(p.getNombre() + " " + p.getApellido());
                dto.setDni(p.getDni());
                dto.setTelefono(p.getTelefono());
            }
        }

        dto.setFecha(historial.getFecha());
        dto.setEdad(historial.getEdad());
        dto.setRecomendaciones(historial.getRecomendaciones());

        // ✅ CAMBIO SOLICITADO: Buscar nombre del EVALUADOR usando su UUID
        if (historial.getEvaluador() != null) {
            try {
                UUID evaluadorUuid = UUID.fromString(historial.getEvaluador());
                usuarioService.findById(evaluadorUuid).ifPresent(user -> {
                    if (user.getPersona() != null) {
                        dto.setEvaluador(user.getPersona().getNombre() + " " + user.getPersona().getApellido());
                    } else {
                        dto.setEvaluador(historial.getEvaluador());
                    }
                });
            } catch (Exception e) {
                // Si no es un UUID válido, dejamos el string tal cual
                dto.setEvaluador(historial.getEvaluador());
            }
        }

        // Análisis de los 4 cuadrantes
        dto.setAnalisisLejosOD(historial.getAnalisisLejosOD());
        dto.setAnalisisLejosOI(historial.getAnalisisLejosOI());
        dto.setAnalisisCercaOD(historial.getAnalisisCercaOD());
        dto.setAnalisisCercaOI(historial.getAnalisisCercaOI());

        dto.setImagenRefraccionUrl(historial.getImagenRefraccionUrl());
        dto.setImagenLenzometriaUrl(historial.getImagenLenzometriaUrl());
        dto.setImagenKeratometriaUrl(historial.getImagenKeratometriaUrl());

        dto.setCreatedAt(historial.getCreatedAt());

        if (historial.getVisionLejos() != null) {
            dto.setVisionLejos(toVisionDTO(historial.getVisionLejos()));
        }

        if (historial.getVisionCerca() != null) {
            dto.setVisionCerca(toVisionDTO(historial.getVisionCerca()));
        }

        return dto;
    }

    private VisionDTO toVisionDTO(Vision v) {
        VisionDTO vdto = new VisionDTO();
        vdto.setOjoIzquierdoEsf(v.getOjoIzquierdoEsf());
        vdto.setOjoDerechoEsf(v.getOjoDerechoEsf());
        vdto.setOjoIzquierdoCil(v.getOjoIzquierdoCil());
        vdto.setOjoDerechoCil(v.getOjoDerechoCil());
        vdto.setOjoIzquierdoEje(v.getOjoIzquierdoEje());
        vdto.setOjoDerechoEje(v.getOjoDerechoEje());
        vdto.setOjoIzquierdoDip(v.getOjoIzquierdoDip());
        vdto.setOjoDerechoDip(v.getOjoDerechoDip());
        vdto.setOjoIzquierdoAv(v.getOjoIzquierdoAv());
        vdto.setOjoDerechoAv(v.getOjoDerechoAv());
        return vdto;
    }

    // Listar usuarios con expediente
    @GetMapping("/usuarios-con-expediente")
    public ResponseEntity<List<SubDatosUserDTO>> listarUsuariosConHistorial() {
        List<Usuario> usuarios = historialService.listarUsuariosConHistorial();

        List<SubDatosUserDTO> dtos = usuarios.stream()
                .map(u -> {
                    SubDatosUserDTO dto = usuarioService.subdatosPorUsuarioId(u.getUsuarioId()).orElse(null);
                    if (dto != null) {
                        List<HistorialOpticoDTO> hists = historialService.listarTodoPorUsuario(u.getUsuarioId());
                        if (!hists.isEmpty()) {
                            dto.setFechaPrimerHistorial(hists.get(0).getCreatedAt());
                        }
                    }
                    return dto;
                })
                .filter(Objects::nonNull)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // Listar por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<HistorialOpticoDTO>> listarPorUsuario(@PathVariable UUID usuarioId) {

        // 1. Llamamos al servicio (que ya nos devuelve la lista de DTOs lista para usar)
        List<HistorialOpticoDTO> lista = historialService.listarTodoPorUsuario(usuarioId);

        // 2. La devolvemos directamente
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable UUID id) {
        historialService.eliminarHistorial(id);

        // ✅ Creamos un Map rápido para enviar el JSON con mensaje
        return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "✅ Historial eliminado correctamente",
                "idEliminado", id
        ));
    }
}