package com.umoar.posmilleniumgames.modulos.inventario.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.repositorios.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return obtenerTodos();
        }
        return productoRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorSku(String sku) {
        return productoRepository.findBySku(sku).orElse(null);
    }

    @Override
    @Transactional
    public Producto guardar(Producto producto) {
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser inferior a cero.");
        }
        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado con el ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hayStockDisponible(Long id, int cantidad) {
        Producto producto = productoRepository.findById(id).orElse(null);
        return producto != null && producto.getStock() >= cantidad;
    }

    @Override
    @Transactional
    public void descontarStock(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a cero.");
        }
        Producto producto = obtenerPorId(id);
        if (producto.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }
}
