package kado.kadosh.controller;

import kado.kadosh.dto.CotizacionRequestDTO;
import kado.kadosh.dto.SubDatosUserDTO;
import kado.kadosh.entities.Cotizacion;
import kado.kadosh.entities.Usuario;
import kado.kadosh.entities.HistorialOptico;
import kado.kadosh.service.CotizacionService;
import kado.kadosh.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    @Autowired private CotizacionService cotizacionService;
    @Autowired private UsuarioService usuarioService;

    // Formateador estándar para Wimiline
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @GetMapping("/usuarios-con-cotizaciones")
    public ResponseEntity<List<Map<String, Object>>> listarUsuariosConCotizacion() {
        List<Usuario> usuarios = cotizacionService.listarUsuariosConCotizacion();

        List<Map<String, Object>> respuesta = usuarios.stream()
                .map(u -> {
                    SubDatosUserDTO completo = usuarioService.subdatosPorUsuarioId(u.getUsuarioId()).orElse(null);

                    if (completo != null) {
                        List<Cotizacion> cotis = cotizacionService.listarPorUsuario(u.getUsuarioId());

                        if (!cotis.isEmpty()) {
                            // Agarramos la última cotización para obtener datos recientes
                            Cotizacion primeraCoti = cotis.get(cotis.size() - 1);
                            HistorialOptico ho = primeraCoti.getHistorialOptico();

                            Map<String, Object> json = new HashMap<>();

                            // 🔥 LÍNEA VITAL: Enviamos el ID al frontend para evitar alertas rojas
                            json.put("usuarioId", u.getUsuarioId());

                            json.put("nombrePaciente", completo.getNombre() + " " + completo.getApellido());

                            String ap = completo.getNombreApoderado();
                            json.put("nombreApoderado", (ap == null || ap.isBlank() || ap.equalsIgnoreCase("Ninguno"))
                                    ? "Ninguno"
                                    : ap + " " + completo.getApellidoApoderado());

                            json.put("dniPaciente", completo.getDni());
                            json.put("telefonoPaciente", completo.getTelefono());
                            json.put("edadPaciente", primeraCoti.getEdad());
                            json.put("correoPaciente", completo.getCorreo());

                            if (primeraCoti.getFechaCreacion() != null) {
                                json.put("fechaInicialCotizacion", primeraCoti.getFechaCreacion().format(FORMATO_FECHA));
                            } else {
                                json.put("fechaInicialCotizacion", "No registrado");
                            }

                            if (ho != null && ho.getFecha() != null) {
                                json.put("fechaHistorialOptico", ho.getFecha().format(FORMATO_FECHA));
                            } else {
                                json.put("fechaHistorialOptico", "No registrado");
                            }

                            return json;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/crear")
    public Cotizacion crear(@RequestBody CotizacionRequestDTO request) {
        return cotizacionService.crearCotizacion(request);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Cotizacion> historial(@PathVariable UUID usuarioId) {
        return cotizacionService.listarPorUsuario(usuarioId);
    }

    @PutMapping("/pagar/{id}")
    public void pagar(@PathVariable UUID id) {
        cotizacionService.registrarPago(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerDetalle(@PathVariable UUID id) {
        Cotizacion c = cotizacionService.buscarPorId(id);

        Map<String, Object> json = new HashMap<>();
        json.put("cotizacionId", c.getCotizacionId());
        json.put("total", c.getTotal());
        json.put("estadoPago", c.getEstadoPago() ? "PAGADO" : "PENDIENTE");

        if (c.getFechaCreacion() != null) {
            json.put("fechaCreacion", c.getFechaCreacion().format(FORMATO_FECHA));
        } else {
            json.put("fechaCreacion", "No registrada");
        }

        json.put("edadAlMomento", c.getEdad());

        // 🔥 ENTRANDO A LAS TABLAS CORRECTAS
        if (c.getUsuario() != null) {
            Usuario u = c.getUsuario();

            // 1. Datos del Paciente (desde Persona)
            if (u.getPersona() != null) {
                json.put("cliente", u.getPersona().getNombre() + " " + u.getPersona().getApellido());
            }

            // 2. Datos del Encargado (desde la entidad Apoderado)
            if (u.getApoderado() != null) {
                // Accedemos a los campos de la tabla Apoderado
                String nomAp = u.getApoderado().getNombre();
                String apeAp = u.getApoderado().getApellido();
                json.put("apoderado", nomAp + " " + (apeAp != null ? apeAp : ""));
            } else {
                json.put("apoderado", null); // No tiene apoderado en la tabla
            }
        }

        // --- Información del Médico ---
        if (c.getHistorialOptico() != null) {
            String evaluadorIdStr = c.getHistorialOptico().getEvaluador();
            try {
                UUID evaluadorUuid = UUID.fromString(evaluadorIdStr);
                usuarioService.subdatosPorUsuarioId(evaluadorUuid).ifPresentOrElse(
                        medico -> json.put("medicoEvaluador", medico.getNombre() + " " + medico.getApellido()),
                        () -> json.put("medicoEvaluador", "Médico no encontrado")
                );
            } catch (Exception e) {
                json.put("medicoEvaluador", evaluadorIdStr);
            }
            json.put("recomendaciones", c.getHistorialOptico().getRecomendaciones());
        }

        // --- Productos y Servicios ---
        List<Map<String, Object>> items = c.getDetalles().stream()
                .filter(d -> Boolean.TRUE.equals(d.getActivo()))
                .map(d -> {
                    Map<String, Object> item = new HashMap<>();
                    String nombre = (d.getProducto() != null)
                            ? d.getProducto().getNombre()
                            : d.getDescripcion();
                    item.put("producto", nombre != null ? nombre : "");
                    item.put("esServicio", d.getProducto() == null);
                    item.put("cantidad", d.getCantidad());
                    item.put("precioUnitario", d.getPrecioCongelado());
                    item.put("subtotal", d.getCantidad() * d.getPrecioCongelado());
                    return item;
                }).toList();

        json.put("productos", items);

        return ResponseEntity.ok(json);
    }
}