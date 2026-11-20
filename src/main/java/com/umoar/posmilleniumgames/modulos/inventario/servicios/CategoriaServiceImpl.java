package com.umoar.posmilleniumgames.modulos.inventario.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Categoria;
import com.umoar.posmilleniumgames.modulos.inventario.repositorios.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional
    public Categoria guardar(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        categoria.setNombre(categoria.getNombre().trim());
        if (categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public Categoria crearSiNoExiste(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        String nombreLimpio = nombre.trim();
        return categoriaRepository.findByNombreIgnoreCase(nombreLimpio)
                .orElseGet(() -> {
                    Categoria nueva = new Categoria();
                    nueva.setNombre(nombreLimpio);
                    return categoriaRepository.save(nueva);
                });
    }
}
