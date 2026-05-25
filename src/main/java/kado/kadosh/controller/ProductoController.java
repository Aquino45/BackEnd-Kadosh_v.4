package kado.kadosh.controller;

import kado.kadosh.entities.Producto;
import kado.kadosh.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    @Autowired private ProductoService productoService;

    @GetMapping("/subcategoria/{subId}")
    public List<Producto> listarPorSub(@PathVariable String subId) {
        // Convertimos el String a UUID manualmente para evitar que Spring explote si el formato falla
        UUID uuid = UUID.fromString(subId);
        return productoService.listarPorSubCategoria(uuid);
    }

    @PostMapping
    public Producto guardar(@RequestBody Producto producto) {
        return productoService.guardar(producto);
    }
}
