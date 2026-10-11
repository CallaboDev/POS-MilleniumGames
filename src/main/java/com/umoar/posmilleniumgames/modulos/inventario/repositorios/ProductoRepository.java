package com.umoar.posmilleniumgames.modulos.inventario.repositorios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockLessThanEqualOrderByStockAsc(Integer stock);

    long countByStockLessThanEqual(Integer stock);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    Optional<Producto> findBySku(String sku);
}