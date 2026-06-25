package kado.kadosh.service;

import kado.kadosh.dto.ChatbotEstadisticasDTO;
import kado.kadosh.dto.ChatbotMensajeRequestDTO;
import kado.kadosh.dto.ChatbotRespuestaDTO;
import kado.kadosh.dto.PreguntaFrecuenteDTO;
import kado.kadosh.entities.HistorialConsulta;
import kado.kadosh.entities.PreguntaFrecuente;

import java.util.List;
import java.util.UUID;

public interface ChatbotService {
    // Chatbot (HU-S3-05)
    ChatbotRespuestaDTO responder(ChatbotMensajeRequestDTO request);

    // CRUD FAQs (HU-S3-05 / HU-S3-06)
    List<PreguntaFrecuente> listarTodas();
    List<PreguntaFrecuente> listarActivas();
    PreguntaFrecuente buscarPorId(UUID id);
    PreguntaFrecuente guardar(PreguntaFrecuenteDTO dto);
    PreguntaFrecuente actualizar(UUID id, PreguntaFrecuenteDTO dto);
    void eliminarLogico(UUID id);

    // Gestión base de conocimiento (HU-S3-06)
    List<PreguntaFrecuente> buscarConFiltros(String q, String categoria, Boolean activo);
    List<String> listarCategorias();
    List<HistorialConsulta> listarHistorial();
    List<HistorialConsulta> listarConsultasSinRespuesta();
    ChatbotEstadisticasDTO obtenerEstadisticas();
}
