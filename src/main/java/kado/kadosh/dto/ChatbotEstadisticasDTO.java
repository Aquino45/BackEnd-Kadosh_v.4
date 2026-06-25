package kado.kadosh.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatbotEstadisticasDTO {
    private long totalPreguntas;
    private long preguntasActivas;
    private long preguntasInactivas;
    private long totalConsultas;
    private long consultasConRespuesta;
    private long consultasSinRespuesta;
    private List<String> categorias;
}
