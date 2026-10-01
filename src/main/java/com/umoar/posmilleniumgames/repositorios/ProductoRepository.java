package com.umoar.posmilleniumgames.repositorios;

import com.umoar.posmilleniumgames.modelos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findBySku(String sku);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
