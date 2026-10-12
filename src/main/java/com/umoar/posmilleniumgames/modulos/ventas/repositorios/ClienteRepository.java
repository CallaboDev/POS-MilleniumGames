package com.umoar.posmilleniumgames.modulos.ventas.repositorios;

import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByNombreCompletoIgnoreCase(String nombreCompleto);
    List<Cliente> findByNombreCompletoContainingIgnoreCase(String termino);
    List<Cliente> findByCorreoContainingIgnoreCase(String correo);
}
