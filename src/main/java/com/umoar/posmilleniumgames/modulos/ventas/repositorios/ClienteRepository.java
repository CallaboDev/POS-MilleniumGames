package com.umoar.posmilleniumgames.modulos.ventas.repositorios;

import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByNombreCompletoIgnoreCase(String nombreCompleto);
    List<Cliente> findByNombreCompletoContainingIgnoreCase(String termino);
    List<Cliente> findByCorreoContainingIgnoreCase(String correo);

    @Query("""
            SELECT c FROM Cliente c
            WHERE LOWER(c.nombreCompleto) LIKE LOWER(CONCAT('%', :termino, '%'))
               OR LOWER(COALESCE(c.correo, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
            """)
    Page<Cliente> buscarPaginado(@Param("termino") String termino, Pageable pageable);
}
