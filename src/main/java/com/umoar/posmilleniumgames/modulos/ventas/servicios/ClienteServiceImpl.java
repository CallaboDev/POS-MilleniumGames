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
                    consumidorFinal.setCorreo("nose@gmail.com");
                    return clienteRepository.save(consumidorFinal);
                });
    }

    @Override
    @Transactional
    public Cliente guardar(Cliente cliente) {
        validar(cliente);
        cliente.setNombreCompleto(cliente.getNombreCompleto().trim());
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public Cliente actualizar(Long id, Cliente cliente) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el cliente."));
        validar(cliente);
        existente.setNombreCompleto(cliente.getNombreCompleto().trim());
        existente.setCorreo(limpiar(cliente.getCorreo()));
        existente.setTelefono(limpiar(cliente.getTelefono()));
        return clienteRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró el cliente.");
        }
        clienteRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listar(String criterio) {
        if (criterio == null || criterio.isBlank()) return obtenerTodos();
        String termino = criterio.trim();
        java.util.LinkedHashMap<Long, Cliente> resultados = new java.util.LinkedHashMap<>();
        clienteRepository.findByNombreCompletoContainingIgnoreCase(termino)
                .forEach(c -> resultados.put(c.getId(), c));
        clienteRepository.findByCorreoContainingIgnoreCase(termino)
                .forEach(c -> resultados.put(c.getId(), c));
        return List.copyOf(resultados.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return obtenerTodos();
        }
        return clienteRepository.findByNombreCompletoContainingIgnoreCase(nombre.trim());
    }

    private void validar(Cliente cliente) {
        if (cliente.getNombreCompleto() == null || cliente.getNombreCompleto().isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        cliente.setCorreo(limpiar(cliente.getCorreo()));
        cliente.setTelefono(limpiar(cliente.getTelefono()));
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
