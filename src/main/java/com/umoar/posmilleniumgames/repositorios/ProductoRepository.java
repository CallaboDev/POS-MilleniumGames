package com.umoar.posmilleniumgames.repositorios;

import com.umoar.posmilleniumgames.modelos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockLessThanEqualOrderByStockAsc(Integer stock);

    long countByStockLessThanEqual(Integer stock);
}
