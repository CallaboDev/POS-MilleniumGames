package com.umoar.posmilleniumgames.modulos.inventario.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Plataforma;
import com.umoar.posmilleniumgames.modulos.inventario.repositorios.PlataformaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlataformaService {

    private final PlataformaRepository plataformaRepository;

    public PlataformaService(PlataformaRepository plataformaRepository) {
        this.plataformaRepository = plataformaRepository;
    }

    @Transactional(readOnly = true)
    public List<Plataforma> obtenerTodas() {
        return plataformaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Plataforma> buscarPorId(Long id) {
        return plataformaRepository.findById(id);
    }

    @Transactional
    public Plataforma guardar(Plataforma plataforma) {
        if (plataforma.getNombre() == null || plataforma.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la plataforma es obligatorio.");
        }
        plataforma.setNombre(plataforma.getNombre().trim());
        if (plataformaRepository.existsByNombreIgnoreCase(plataforma.getNombre())) {
            throw new IllegalArgumentException("Ya existe una plataforma con el nombre: " + plataforma.getNombre());
        }
        return plataformaRepository.save(plataforma);
    }

    @Transactional
    public Plataforma crearSiNoExiste(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        String nombreLimpio = nombre.trim();
        return plataformaRepository.findByNombreIgnoreCase(nombreLimpio)
                .orElseGet(() -> {
                    Plataforma nueva = new Plataforma();
                    nueva.setNombre(nombreLimpio);
                    return plataformaRepository.save(nueva);
                });
    }
}
