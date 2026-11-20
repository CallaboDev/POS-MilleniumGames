package com.umoar.posmilleniumgames.modulos.inventario.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Plataforma;

import java.util.List;
import java.util.Optional;

public interface PlataformaService {

    List<Plataforma> obtenerTodas();

    Optional<Plataforma> buscarPorId(Long id);

    Plataforma guardar(Plataforma plataforma);

    Plataforma crearSiNoExiste(String nombre);
}
