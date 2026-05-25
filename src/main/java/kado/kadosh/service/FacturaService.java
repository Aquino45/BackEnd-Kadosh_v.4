package kado.kadosh.service;

import kado.kadosh.dto.FacturaRequestDTO;
import kado.kadosh.entities.Factura;

import java.util.List;
import java.util.UUID;

public interface FacturaService {
    Factura emitir(FacturaRequestDTO dto);
    List<Factura> listar();
    Factura buscarPorId(UUID id);
    List<Factura> buscarPorUsuario(UUID usuarioId);
    List<Factura> buscarPorEstado(String estado);
    void anular(UUID facturaId);
}
