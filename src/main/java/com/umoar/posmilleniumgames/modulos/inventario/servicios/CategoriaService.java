package com.umoar.posmilleniumgames.modulos.inventario.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaService {

    List<Categoria> obtenerTodas();

    Optional<Categoria> buscarPorId(Long id);

    Categoria guardar(Categoria categoria);

    Categoria crearSiNoExiste(String nombre);
}
