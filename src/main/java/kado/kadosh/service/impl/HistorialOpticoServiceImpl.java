package kado.kadosh.service.impl;

import kado.kadosh.dto.HistorialOpticoDTO;
import kado.kadosh.dto.VisionDTO;
import kado.kadosh.entities.HistorialOptico;
import kado.kadosh.entities.Usuario;
import kado.kadosh.entities.Vision;
import kado.kadosh.repository.HistorialOpticoRepository;
import kado.kadosh.repository.VisionRepository;
import kado.kadosh.repository.UsuarioRepository; // ✅ Agregado
import kado.kadosh.service.HistorialOpticoService;
import kado.kadosh.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder; // ✅ Agregado
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class HistorialOpticoServiceImpl implements HistorialOpticoService {

    @Autowired
    private HistorialOpticoRepository historialOpticoRepository;

    @Autowired
    private VisionRepository visionRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository; // ✅ Agregado

    @Override
    public HistorialOptico crearHistorial(HistorialOpticoDTO dto) {
        // ✅ EXTRAER UUID DIRECTO DEL TOKEN (Ya que es tu Subject)
        String uuidDesdeToken = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = null;
        if (dto.getUsuarioId() != null) {
            usuario = usuarioService.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + dto.getUsuarioId()));
        }

        Vision visionLejos = new Vision();
        visionLejos.setVisionId(UUID.randomUUID());
        visionLejos.setTipoVision("lejos");
        if (dto.getVisionLejos() != null) mapVision(dto.getVisionLejos(), visionLejos);
        visionLejos = visionRepository.save(visionLejos);

        Vision visionCerca = new Vision();
        visionCerca.setVisionId(UUID.randomUUID());
        visionCerca.setTipoVision("cerca");
        if (dto.getVisionCerca() != null) mapVision(dto.getVisionCerca(), visionCerca);
        visionCerca = visionRepository.save(visionCerca);

        HistorialOptico historial = new HistorialOptico();
        historial.setHistorialOpticoId(UUID.randomUUID());

        if (dto.getFecha() != null) {
            if (dto.getFecha().getHour() == 0 && dto.getFecha().getMinute() == 0) {
                LocalDateTime ahora = LocalDateTime.now();
                historial.setFecha(dto.getFecha().withHour(ahora.getHour()).withMinute(ahora.getMinute()).withSecond(ahora.getSecond()));
            } else {
                historial.setFecha(dto.getFecha());
            }
        } else {
            historial.setFecha(LocalDateTime.now());
        }

        historial.setEdad(dto.getEdad());
        historial.setVisionLejos(visionLejos);
        historial.setVisionCerca(visionCerca);
        historial.setAnalisisLejosOD(dto.getAnalisisLejosOD());
        historial.setAnalisisLejosOI(dto.getAnalisisLejosOI());
        historial.setAnalisisCercaOD(dto.getAnalisisCercaOD());
        historial.setAnalisisCercaOI(dto.getAnalisisCercaOI());
        historial.setRecomendaciones(dto.getRecomendaciones());

        // ✅ ASIGNAMOS EL UUID DIRECTAMENTE
        historial.setEvaluador(uuidDesdeToken);

        historial.setImagenRefraccionUrl(dto.getImagenRefraccionUrl());
        historial.setImagenLenzometriaUrl(dto.getImagenLenzometriaUrl());
        historial.setImagenKeratometriaUrl(dto.getImagenKeratometriaUrl());

        if (usuario != null) historial.setUsuario(usuario);

        return historialOpticoRepository.save(historial);
    }

    @Override
    public HistorialOptico actualizarHistorial(UUID id, HistorialOpticoDTO dto) {
        HistorialOptico historial = historialOpticoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historial óptico no encontrado con id: " + id));

        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioService.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + dto.getUsuarioId()));
            historial.setUsuario(usuario);
        }

        if (dto.getFecha() != null) historial.setFecha(dto.getFecha());
        if (dto.getEdad() != null) historial.setEdad(dto.getEdad());
        if (dto.getRecomendaciones() != null) historial.setRecomendaciones(dto.getRecomendaciones());
        if (dto.getEvaluador() != null) historial.setEvaluador(dto.getEvaluador());

        if (dto.getAnalisisLejosOD() != null) historial.setAnalisisLejosOD(dto.getAnalisisLejosOD());
        if (dto.getAnalisisLejosOI() != null) historial.setAnalisisLejosOI(dto.getAnalisisLejosOI());
        if (dto.getAnalisisCercaOD() != null) historial.setAnalisisCercaOD(dto.getAnalisisCercaOD());
        if (dto.getAnalisisCercaOI() != null) historial.setAnalisisCercaOI(dto.getAnalisisCercaOI());

        if (dto.getImagenRefraccionUrl() != null) historial.setImagenRefraccionUrl(dto.getImagenRefraccionUrl());
        if (dto.getImagenLenzometriaUrl() != null) historial.setImagenLenzometriaUrl(dto.getImagenLenzometriaUrl());
        if (dto.getImagenKeratometriaUrl() != null) historial.setImagenKeratometriaUrl(dto.getImagenKeratometriaUrl());

        if (dto.getVisionLejos() != null) {
            if (historial.getVisionLejos() == null) {
                Vision vl = new Vision(); vl.setVisionId(UUID.randomUUID()); vl.setTipoVision("lejos");
                historial.setVisionLejos(vl);
            }
            mapVision(dto.getVisionLejos(), historial.getVisionLejos());
            visionRepository.save(historial.getVisionLejos());
        }

        if (dto.getVisionCerca() != null) {
            if (historial.getVisionCerca() == null) {
                Vision vc = new Vision(); vc.setVisionId(UUID.randomUUID()); vc.setTipoVision("cerca");
                historial.setVisionCerca(vc);
            }
            mapVision(dto.getVisionCerca(), historial.getVisionCerca());
            visionRepository.save(historial.getVisionCerca());
        }

        return historialOpticoRepository.save(historial);
    }

    // ==========================================
    // 🔁 MAPPER ENTITY → DTO (CON EL CAMBIO APLICADO)
    // ==========================================
    private HistorialOpticoDTO toDTO(HistorialOptico historial) {
        if (historial == null) return null;

        HistorialOpticoDTO dto = new HistorialOpticoDTO();
        dto.setHistorialOpticoId(historial.getHistorialOpticoId());
        dto.setFecha(historial.getFecha());
        dto.setCreatedAt(historial.getCreatedAt());
        dto.setEdad(historial.getEdad());

        // 1. Datos del PACIENTE
        if (historial.getUsuario() != null && historial.getUsuario().getPersona() != null) {
            var p = historial.getUsuario().getPersona();
            dto.setUsuarioId(historial.getUsuario().getUsuarioId());
            dto.setPaciente(p.getNombre() + " " + p.getApellido());
            dto.setDni(p.getDni());
            dto.setTelefono(p.getTelefono());
        }

        // 🔴 2. CAMBIO AQUÍ: LÓGICA PARA EL EVALUADOR (Doctor/Optometrista)
        String evaluadorRaw = historial.getEvaluador(); // Trae el UUID (ej: "a5d13...")

        if (evaluadorRaw != null && !evaluadorRaw.isBlank()) {
            try {
                // Convertimos el texto a UUID
                UUID evaluadorId = UUID.fromString(evaluadorRaw);

                // Buscamos al usuario en la BD usando el repositorio que ya tienes inyectado
                usuarioRepository.findById(evaluadorId).ifPresentOrElse(
                        (usuarioEvaluador) -> {
                            // Si existe y tiene datos personales, ponemos su nombre
                            if (usuarioEvaluador.getPersona() != null) {
                                String nombreCompleto = usuarioEvaluador.getPersona().getNombre() + " " +
                                        usuarioEvaluador.getPersona().getApellido();
                                dto.setEvaluador(nombreCompleto);
                            } else {
                                dto.setEvaluador("Evaluador (Sin datos personales)");
                            }
                        },
                        () -> {
                            dto.setEvaluador("Usuario no encontrado");
                        }
                );
            } catch (IllegalArgumentException e) {
                // Si no era un UUID válido, lo dejamos como estaba
                dto.setEvaluador(evaluadorRaw);
            }
        } else {
            dto.setEvaluador("Sin evaluador");
        }
        // ---------------------------------------------------------

        dto.setAnalisisLejosOD(historial.getAnalisisLejosOD());
        dto.setAnalisisLejosOI(historial.getAnalisisLejosOI());
        dto.setAnalisisCercaOD(historial.getAnalisisCercaOD());
        dto.setAnalisisCercaOI(historial.getAnalisisCercaOI());
        dto.setRecomendaciones(historial.getRecomendaciones());

        dto.setImagenRefraccionUrl(historial.getImagenRefraccionUrl());
        dto.setImagenLenzometriaUrl(historial.getImagenLenzometriaUrl());
        dto.setImagenKeratometriaUrl(historial.getImagenKeratometriaUrl());

        if (historial.getVisionLejos() != null) dto.setVisionLejos(toVisionDTO(historial.getVisionLejos()));
        if (historial.getVisionCerca() != null) dto.setVisionCerca(toVisionDTO(historial.getVisionCerca()));

        return dto;
    }

    private void mapVision(VisionDTO dto, Vision entity) {
        entity.setOjoIzquierdoEsf(dto.getOjoIzquierdoEsf());
        entity.setOjoDerechoEsf(dto.getOjoDerechoEsf());
        entity.setOjoIzquierdoCil(dto.getOjoIzquierdoCil());
        entity.setOjoDerechoCil(dto.getOjoDerechoCil());
        entity.setOjoIzquierdoEje(dto.getOjoIzquierdoEje());
        entity.setOjoDerechoEje(dto.getOjoDerechoEje());
        entity.setOjoIzquierdoDip(dto.getOjoIzquierdoDip());
        entity.setOjoDerechoDip(dto.getOjoDerechoDip());
        entity.setOjoIzquierdoAv(dto.getOjoIzquierdoAv());
        entity.setOjoDerechoAv(dto.getOjoDerechoAv());
    }

    private VisionDTO toVisionDTO(Vision v) {
        VisionDTO dto = new VisionDTO();
        dto.setOjoIzquierdoEsf(v.getOjoIzquierdoEsf());
        dto.setOjoDerechoEsf(v.getOjoDerechoEsf());
        dto.setOjoIzquierdoCil(v.getOjoIzquierdoCil());
        dto.setOjoDerechoCil(v.getOjoDerechoCil());
        dto.setOjoIzquierdoEje(v.getOjoIzquierdoEje());
        dto.setOjoDerechoEje(v.getOjoDerechoEje());
        dto.setOjoIzquierdoDip(v.getOjoIzquierdoDip());
        dto.setOjoDerechoDip(v.getOjoDerechoDip());
        dto.setOjoIzquierdoAv(v.getOjoIzquierdoAv());
        dto.setOjoDerechoAv(v.getOjoDerechoAv());
        return dto;
    }

    // Cambia la firma para devolver List<HistorialOpticoDTO>
    @Override
    public List<HistorialOpticoDTO> listarTodoPorUsuario(UUID usuarioId) {
        // 1. Buscas las entidades
        List<HistorialOptico> entidades = historialOpticoRepository.findByUsuarioUsuarioIdOrderByFechaDesc(usuarioId);

        // 2. Las conviertes a DTO usando tu método 'toDTO'
        return entidades.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<Usuario> listarUsuariosConHistorial() {
        return historialOpticoRepository.findUsuariosConHistorial();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void eliminarHistorial(UUID id) {
        if (!historialOpticoRepository.existsById(id)) {
            throw new RuntimeException("Historial no encontrado con id: " + id);
        }
        historialOpticoRepository.deleteById(id); // Borra historial + visiones por la cascada
    }
}