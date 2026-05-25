package kado.kadosh.controller;

import kado.kadosh.entities.Categoria;
import kado.kadosh.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    @Autowired private CategoriaService categoriaService;

    @GetMapping("/activas")
    public List<Categoria> listarActivas() {
        return categoriaService.listarActivas();
    }

    @PostMapping
    public Categoria guardar(@RequestBody Categoria categoria) {
        return categoriaService.guardar(categoria);
    }
}