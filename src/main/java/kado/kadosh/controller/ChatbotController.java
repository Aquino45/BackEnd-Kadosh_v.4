package kado.kadosh.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kado.kadosh.dto.ChatbotEstadisticasDTO;
import kado.kadosh.dto.ChatbotMensajeRequestDTO;
import kado.kadosh.dto.ChatbotRespuestaDTO;
import kado.kadosh.dto.PreguntaFrecuenteDTO;
import kado.kadosh.entities.HistorialConsulta;
import kado.kadosh.entities.PreguntaFrecuente;
import kado.kadosh.service.ChatbotService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    // ──────────────────────────────────────────────────────────────
    // CHATBOT (cualquier usuario autenticado)
    // ──────────────────────────────────────────────────────────────

    @PostMapping("/preguntar")
    public ResponseEntity<ChatbotRespuestaDTO> preguntar(@RequestBody ChatbotMensajeRequestDTO request) {
        return ResponseEntity.ok(chatbotService.responder(request));
    }

    @GetMapping("/preguntas/activas")
    public ResponseEntity<List<PreguntaFrecuente>> listarActivas() {
        return ResponseEntity.ok(chatbotService.listarActivas());
    }

    // ──────────────────────────────────────────────────────────────
    // GESTIÓN DE FAQs (solo ADMIN)
    // ──────────────────────────────────────────────────────────────

    @GetMapping("/preguntas")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<PreguntaFrecuente>> listarTodas() {
        return ResponseEntity.ok(chatbotService.listarTodas());
    }

    @GetMapping("/preguntas/buscar")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<PreguntaFrecuente>> buscarConFiltros(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean activo) {
        return ResponseEntity.ok(chatbotService.buscarConFiltros(q, categoria, activo));
    }

    @GetMapping("/preguntas/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<PreguntaFrecuente> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(chatbotService.buscarPorId(id));
    }

    @PostMapping("/preguntas")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<PreguntaFrecuente> crear(@RequestBody PreguntaFrecuenteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatbotService.guardar(dto));
    }

    @PutMapping("/preguntas/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<PreguntaFrecuente> actualizar(
            @PathVariable UUID id,
            @RequestBody PreguntaFrecuenteDTO dto) {
        return ResponseEntity.ok(chatbotService.actualizar(id, dto));
    }

    @DeleteMapping("/preguntas/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable UUID id) {
        chatbotService.eliminarLogico(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Pregunta frecuente eliminada correctamente",
                "idEliminado", id.toString()
        ));
    }

    @GetMapping("/categorias")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<String>> listarCategorias() {
        return ResponseEntity.ok(chatbotService.listarCategorias());
    }

    // ──────────────────────────────────────────────────────────────
    // BASE DE CONOCIMIENTO - ANÁLISIS (solo ADMIN)
    // ──────────────────────────────────────────────────────────────

    @GetMapping("/historial")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<HistorialConsulta>> listarHistorial() {
        return ResponseEntity.ok(chatbotService.listarHistorial());
    }

    @GetMapping("/historial/sin-respuesta")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<HistorialConsulta>> consultasSinRespuesta() {
        return ResponseEntity.ok(chatbotService.listarConsultasSinRespuesta());
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ChatbotEstadisticasDTO> estadisticas() {
        return ResponseEntity.ok(chatbotService.obtenerEstadisticas());
    }
}
