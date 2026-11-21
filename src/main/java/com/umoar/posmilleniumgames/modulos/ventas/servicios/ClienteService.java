package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> obtenerTodos();
    Page<Cliente> listarPaginado(String criterio, Pageable pageable);
    Optional<Cliente> buscarPorId(Long id);
    Cliente obtenerOCrearConsumidorFinal();
    Cliente guardar(Cliente cliente);
    Cliente actualizar(Long id, Cliente cliente);
    void eliminar(Long id);
    List<Cliente> listar(String criterio);
    List<Cliente> buscarPorNombre(String nombre);
}
