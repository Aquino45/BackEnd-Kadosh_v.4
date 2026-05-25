package kado.kadosh.controller;

import kado.kadosh.entities.SubCategoria;
import kado.kadosh.service.SubCategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subcategorias")
@RequiredArgsConstructor
public class SubCategoriaController {

    @Autowired private SubCategoriaService subService;

    @GetMapping("/categoria/{categoriaId}")
    public List<SubCategoria> listarPorPadre(@PathVariable UUID categoriaId) {
        return subService.listarPorCategoria(categoriaId);
    }

    @PostMapping
    public SubCategoria guardar(@RequestBody SubCategoria sub) {
        return subService.guardar(sub);
    }
}