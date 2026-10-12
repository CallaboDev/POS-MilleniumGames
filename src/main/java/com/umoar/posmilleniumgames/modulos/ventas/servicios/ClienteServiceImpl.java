package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;
import com.umoar.posmilleniumgames.modulos.ventas.repositorios.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    @Transactional
    public Cliente obtenerOCrearConsumidorFinal() {
        return clienteRepository.findByNombreCompletoIgnoreCase("Consumidor Final")
                .orElseGet(() -> {
                    Cliente consumidorFinal = new Cliente();
                    consumidorFinal.setNombreCompleto("Consumidor Final");
                    consumidorFinal.setTelefono("N/A");
                    consumidorFinal.setCorreo("consumidorfinal@milleniumgames.com");
                    return clienteRepository.save(consumidorFinal);
                });
    }

    @Override
    @Transactional
    public Cliente guardar(Cliente cliente) {
        if (cliente.getNombreCompleto() == null || cliente.getNombreCompleto().isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return obtenerTodos();
        }
        return clienteRepository.findByNombreCompletoContainingIgnoreCase(nombre.trim());
    }
}
