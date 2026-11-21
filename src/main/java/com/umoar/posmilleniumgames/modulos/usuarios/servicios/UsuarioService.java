package com.umoar.posmilleniumgames.modulos.usuarios.servicios;

import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Rol;
import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> listarUsuarios();

    Page<Usuario> listarUsuariosPaginado(Pageable pageable);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorUsername(String username);

    boolean existePorUsername(String username);

    Optional<Rol> buscarRolPorNombre(String nombre);

    Usuario guardar(Usuario usuario);

    Usuario actualizar(Usuario usuario);

    void eliminar(Long id);
}
