package kado.kadosh.controller;

import kado.kadosh.dto.DetalleFacturaResponseDTO;
import kado.kadosh.dto.FacturaRequestDTO;
import kado.kadosh.dto.FacturaResponseDTO;
import kado.kadosh.entities.DetalleFactura;
import kado.kadosh.entities.Factura;
import kado.kadosh.entities.Persona;
import kado.kadosh.service.FacturaService;
import kado.kadosh.service.PdfFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private PdfFacturaService pdfFacturaService;

    @PostMapping
    public ResponseEntity<?> emitir(@RequestBody FacturaRequestDTO dto) {
        try {
            Factura factura = facturaService.emitir(dto);
            return ResponseEntity.ok(toResponse(factura));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        List<FacturaResponseDTO> response = facturaService.listar().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(toResponse(facturaService.buscarPorId(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> buscarPorUsuario(@PathVariable UUID usuarioId) {
        List<FacturaResponseDTO> response = facturaService.buscarPorUsuario(usuarioId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorEstado(@RequestParam String estado) {
        List<FacturaResponseDTO> response = facturaService.buscarPorEstado(estado).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('Admin', 'Optometrista', 'Cliente')")
    public ResponseEntity<?> descargarPdf(@PathVariable UUID id, Authentication auth) {
        try {
            Factura factura = facturaService.buscarPorId(id);

            boolean esPrivilegiado = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_Admin") ||
                                  a.getAuthority().equals("ROLE_Optometrista"));

            if (!esPrivilegiado) {
                String currentUserId = auth.getName();
                if (factura.getUsuario() == null ||
                    !factura.getUsuario().getUsuarioId().toString().equals(currentUserId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "No tienes permiso para ver esta factura"));
                }
            }

            byte[] pdf = pdfFacturaService.generar(factura);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + factura.getNumero() + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/anular/{id}")
    public ResponseEntity<?> anular(@PathVariable UUID id) {
        try {
            facturaService.anular(id);
            return ResponseEntity.ok(Map.of("mensaje", "Factura anulada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private FacturaResponseDTO toResponse(Factura factura) {
        FacturaResponseDTO dto = new FacturaResponseDTO();
        dto.setFacturaId(factura.getFacturaId());
        if (factura.getCotizacion() != null) {
            dto.setCotizacionId(factura.getCotizacion().getCotizacionId());
        }
        dto.setNumero(factura.getNumero());
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setSubtotal(factura.getSubtotal());
        dto.setIgv(factura.getIgv());
        dto.setTotal(factura.getTotal());
        dto.setEstado(factura.getEstado());

        if (factura.getUsuario() != null && factura.getUsuario().getPersona() != null) {
            Persona p = factura.getUsuario().getPersona();
            dto.setClienteNombre(p.getNombre() + " " + p.getApellido());
            dto.setClienteDni(p.getDni());
        }

        if (factura.getDetalles() != null) {
            dto.setDetalles(factura.getDetalles().stream()
                    .filter(d -> Boolean.TRUE.equals(d.getActivo()))
                    .map(this::toDetalleResponse)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private DetalleFacturaResponseDTO toDetalleResponse(DetalleFactura d) {
        DetalleFacturaResponseDTO dDto = new DetalleFacturaResponseDTO();
        String nombre = d.getProducto() != null ? d.getProducto().getNombre() : d.getDescripcion();
        dDto.setProductoNombre(nombre != null ? nombre : "");
        dDto.setCantidad(d.getCantidad());
        dDto.setPrecioUnitario(d.getPrecioUnitario());
        dDto.setSubtotal(d.getSubtotal());
        return dDto;
    }
}
