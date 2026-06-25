package kado.kadosh.service.impl;

import kado.kadosh.dto.ChatbotEstadisticasDTO;
import kado.kadosh.dto.ChatbotMensajeRequestDTO;
import kado.kadosh.dto.ChatbotRespuestaDTO;
import kado.kadosh.dto.PreguntaFrecuenteDTO;
import kado.kadosh.entities.HistorialConsulta;
import kado.kadosh.entities.PreguntaFrecuente;
import kado.kadosh.repository.HistorialConsultaRepository;
import kado.kadosh.repository.PreguntaFrecuenteRepository;
import kado.kadosh.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final PreguntaFrecuenteRepository repo;
    private final HistorialConsultaRepository historialRepo;

    private static final String FALLBACK_MSG =
            "Lo sentimos, no encontramos una respuesta para tu consulta. " +
            "Por favor contáctanos directamente o intenta con otras palabras.";

    private static final Set<String> STOP_WORDS = Set.of(
            "el", "la", "los", "las", "de", "del", "en", "es", "un", "una",
            "que", "y", "a", "con", "por", "para", "se", "su", "sus", "mi",
            "me", "te", "le", "nos", "les", "al", "o", "u", "si", "no",
            "como", "cual", "cuando", "donde", "quien", "hay", "tiene",
            "puedo", "puede", "quiero", "necesito", "saber"
    );

    @Override
    public ChatbotRespuestaDTO responder(ChatbotMensajeRequestDTO request) {
        ChatbotRespuestaDTO response = new ChatbotRespuestaDTO();
        response.setMensajeUsuario(request.getMensaje());

        String normalizado = normalizar(request.getMensaje());
        String[] tokens = normalizado.split("\\s+");

        Map<UUID, Integer> puntaje = new LinkedHashMap<>();

        for (String token : tokens) {
            if (token.length() <= 2 || STOP_WORDS.contains(token)) continue;

            List<PreguntaFrecuente> coincidencias = repo.buscarPorKeyword(token);
            for (PreguntaFrecuente p : coincidencias) {
                UUID pid = p.getPreguntaId();
                if (pid != null) puntaje.merge(pid, 1, (a, b) -> a + b);
            }
        }

        List<ChatbotRespuestaDTO.RespuestaItemDTO> items;

        if (puntaje.isEmpty()) {
            items = List.of();
        } else {
            List<UUID> topIds = puntaje.entrySet().stream()
                    .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                    .limit(3)
                    .map(Map.Entry::getKey)
                    .toList();

            items = topIds.stream()
                    .filter(Objects::nonNull)
                    .map(id -> repo.findById(Objects.requireNonNull(id)).orElse(null))
                    .filter(Objects::nonNull)
                    .map(p -> {
                        ChatbotRespuestaDTO.RespuestaItemDTO item = new ChatbotRespuestaDTO.RespuestaItemDTO();
                        item.setPregunta(p.getPregunta());
                        item.setRespuesta(p.getRespuesta());
                        item.setCategoria(p.getCategoria());
                        return item;
                    })
                    .collect(Collectors.toList());
        }

        response.setEncontrado(!items.isEmpty());
        response.setRespuestas(items);
        if (items.isEmpty()) {
            response.setMensajeFallback(FALLBACK_MSG);
        }

        guardarHistorial(request.getMensaje(), !items.isEmpty(), items.size());

        return response;
    }

    @Override
    public List<PreguntaFrecuente> listarTodas() {
        return repo.findAll();
    }

    @Override
    public List<PreguntaFrecuente> listarActivas() {
        return repo.findByActivoTrue();
    }

    @Override
    public PreguntaFrecuente buscarPorId(UUID id) {
        return repo.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Pregunta frecuente no encontrada con id: " + id));
    }

    @Override
    public PreguntaFrecuente guardar(PreguntaFrecuenteDTO dto) {
        PreguntaFrecuente entidad = new PreguntaFrecuente();
        entidad.setPreguntaId(UUID.randomUUID());
        entidad.setPregunta(dto.getPregunta());
        entidad.setRespuesta(dto.getRespuesta());
        entidad.setPalabrasClave(dto.getPalabrasClave());
        entidad.setCategoria(dto.getCategoria());
        entidad.setActivo(true);
        entidad.setCreadoEn(LocalDateTime.now());
        return repo.save(entidad);
    }

    @Override
    public PreguntaFrecuente actualizar(UUID id, PreguntaFrecuenteDTO dto) {
        PreguntaFrecuente entidad = repo.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Pregunta frecuente no encontrada con id: " + id));
        entidad.setPregunta(dto.getPregunta());
        entidad.setRespuesta(dto.getRespuesta());
        entidad.setPalabrasClave(dto.getPalabrasClave());
        entidad.setCategoria(dto.getCategoria());
        if (dto.getActivo() != null) {
            entidad.setActivo(dto.getActivo());
        }
        return repo.save(entidad);
    }

    @Override
    public void eliminarLogico(UUID id) {
        PreguntaFrecuente entidad = repo.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Pregunta frecuente no encontrada con id: " + id));
        entidad.setActivo(false);
        repo.save(entidad);
    }

    @Override
    public List<PreguntaFrecuente> buscarConFiltros(String q, String categoria, Boolean activo) {
        String qNorm = (q != null && !q.isBlank()) ? q.trim() : null;
        String catNorm = (categoria != null && !categoria.isBlank()) ? categoria.trim() : null;
        return repo.buscarConFiltros(qNorm, catNorm, activo);
    }

    @Override
    public List<String> listarCategorias() {
        return repo.findDistinctCategorias();
    }

    @Override
    public List<HistorialConsulta> listarHistorial() {
        return historialRepo.findAllByOrderByFechaConsultaDesc();
    }

    @Override
    public List<HistorialConsulta> listarConsultasSinRespuesta() {
        return historialRepo.findByRespuestaEncontradaFalseOrderByFechaConsultaDesc();
    }

    @Override
    public ChatbotEstadisticasDTO obtenerEstadisticas() {
        ChatbotEstadisticasDTO stats = new ChatbotEstadisticasDTO();

        List<PreguntaFrecuente> todas = repo.findAll();
        long activas = todas.stream().filter(p -> Boolean.TRUE.equals(p.getActivo())).count();

        stats.setTotalPreguntas(todas.size());
        stats.setPreguntasActivas(activas);
        stats.setPreguntasInactivas(todas.size() - activas);

        List<HistorialConsulta> historial = historialRepo.findAll();
        long conRespuesta = historial.stream().filter(h -> Boolean.TRUE.equals(h.getRespuestaEncontrada())).count();

        stats.setTotalConsultas(historial.size());
        stats.setConsultasConRespuesta(conRespuesta);
        stats.setConsultasSinRespuesta(historial.size() - conRespuesta);

        stats.setCategorias(repo.findDistinctCategorias());

        return stats;
    }

    private void guardarHistorial(String mensaje, boolean encontrado, int coincidencias) {
        try {
            String usuarioIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
            UUID usuarioId = UUID.fromString(usuarioIdStr);

            HistorialConsulta historial = new HistorialConsulta();
            historial.setHistorialId(UUID.randomUUID());
            historial.setMensajeUsuario(mensaje);
            historial.setRespuestaEncontrada(encontrado);
            historial.setCantidadCoincidencias(coincidencias);
            historial.setUsuarioId(usuarioId);
            historial.setFechaConsulta(LocalDateTime.now());

            historialRepo.save(historial);
        } catch (Exception ignored) {
            // El fallo en el historial no debe interrumpir la respuesta al usuario
        }
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase().replaceAll("[^a-z0-9\\s]", "").trim();
    }
}
