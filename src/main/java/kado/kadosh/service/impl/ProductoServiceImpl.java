package kado.kadosh.service.impl;

import kado.kadosh.entities.Producto;
import kado.kadosh.entities.SubCategoria;
import kado.kadosh.repository.ProductoRepository;
import kado.kadosh.repository.SubCategoriaRepository; // 👈 Agregado para la validación
import kado.kadosh.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private SubCategoriaRepository subRepo; // 👈 Necesitamos esto para consultar al "padre" real

    @Override
    public List<Producto> listarPorCategoria(UUID categoriaId) {
        return productoRepo.findByCategoria_CategoriaIdAndActivoTrue(categoriaId);
    }

    @Override
    public List<Producto> listarPorSubCategoria(UUID subCategoriaId) {
        return productoRepo.findBySubCategoria_SubCategoriaIdAndActivoTrue(subCategoriaId);
    }

    @Override
    public Producto buscarPorId(UUID id) {
        return productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Producto guardar(Producto producto) {
        // 🛡️ EL CANDADO DE INTEGRIDAD LÓGICA
        // 1. Buscamos la subcategoría en la BD para ver quién es su padre de verdad
        SubCategoria subBd = subRepo.findById(producto.getSubCategoria().getSubCategoriaId())
                .orElseThrow(() -> new RuntimeException("Error: La subcategoría enviada no existe en la base de datos."));

        // 2. Comparamos el ID de la categoría que envías con el ID de la categoría que la subcategoría tiene en la BD
        UUID idCatEnviada = producto.getCategoria().getCategoriaId();
        UUID idCatRealDeLaSub = subBd.getCategoria().getCategoriaId();

        if (!idCatRealDeLaSub.equals(idCatEnviada)) {
            throw new RuntimeException("¡ERROR DE NEGOCIO! La subcategoría '" + subBd.getNombre() +
                    "' NO pertenece a la categoría enviada. Pertenece a la categoría con ID: " + idCatRealDeLaSub);
        }

        // 3. Si pasó el candado, recién procedemos a guardar
        if (producto.getProductoId() == null) {
            producto.setProductoId(UUID.randomUUID());
        }
        return productoRepo.save(producto);
    }

    @Override
    @Transactional
    public void reducirStock(UUID productoId, Integer cantidad) {
        Producto prod = buscarPorId(productoId);
        if (prod.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para el producto: " + prod.getNombre());
        }
        prod.setStock(prod.getStock() - cantidad);
        productoRepo.save(prod);
    }

    @Override
    @Transactional
    public void restaurarStock(UUID productoId, Integer cantidad) {
        Producto prod = buscarPorId(productoId);
        prod.setStock(prod.getStock() + cantidad);
        productoRepo.save(prod);
    }
}