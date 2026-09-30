package com.umoar.posmilleniumgames.servicios;

import com.umoar.posmilleniumgames.modelos.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> listarUsuarios();

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorUsername(String username);

    Optional<Usuario> buscarPorUsername(String username);

    boolean existePorUsername(String username);

    Usuario guardar(Usuario usuario);

    Usuario actualizar(Usuario usuario);

    void eliminar(Long id);
}
