package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> obtenerTodos();
    Optional<Cliente> buscarPorId(Long id);
    Cliente obtenerOCrearConsumidorFinal();
    Cliente guardar(Cliente cliente);
    List<Cliente> buscarPorNombre(String nombre);
}
