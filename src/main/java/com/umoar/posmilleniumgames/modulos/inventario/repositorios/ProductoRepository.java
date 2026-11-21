package com.umoar.posmilleniumgames.modulos.inventario.repositorios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockLessThanEqualOrderByStockAsc(Integer stock);

    Page<Producto> findByStockLessThanEqual(Integer stock, Pageable pageable);

    long countByStockLessThanEqual(Integer stock);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    Optional<Producto> findBySku(String sku);

    @EntityGraph(attributePaths = {"plataforma", "categoria"})
    Optional<Producto> findFirstBySkuIgnoreCase(String sku);

    @Override
    @EntityGraph(attributePaths = {"plataforma", "categoria"})
    Page<Producto> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"plataforma", "categoria"})
    @Query("""
            SELECT p FROM Producto p
            WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))
               OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :termino, '%'))
            """)
    Page<Producto> buscarPaginado(@Param("termino") String termino, Pageable pageable);
}
