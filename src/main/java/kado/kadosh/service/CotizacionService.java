package kado.kadosh.service;

import kado.kadosh.dto.CotizacionRequestDTO; // El JSON que viene de Angular
import kado.kadosh.entities.Cotizacion;
import kado.kadosh.entities.Usuario;
import java.util.List;
import java.util.UUID;

public interface CotizacionService {
    // Procesa el JSON, calcula totales, congela precios y resta stock
    Cotizacion crearCotizacion(CotizacionRequestDTO dto);

    // Para que el cliente vea su historial mediante su UUID del token
    List<Cotizacion> listarPorUsuario(UUID usuarioId);

    Cotizacion buscarPorId(UUID id);
    void registrarPago(UUID cotizacionId); // Para cuando integres la pasarela después

    // ✅ Agrega esta línea
    List<Usuario> listarUsuariosConCotizacion();


}