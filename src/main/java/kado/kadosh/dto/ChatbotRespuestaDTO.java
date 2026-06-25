package kado.kadosh.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatbotRespuestaDTO {
    private String mensajeUsuario;
    private List<RespuestaItemDTO> respuestas;
    private boolean encontrado;
    private String mensajeFallback;

    @Data
    public static class RespuestaItemDTO {
        private String pregunta;
        private String respuesta;
        private String categoria;
    }
}
