package com.umoar.posmilleniumgames.repositorios;

import com.umoar.posmilleniumgames.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

    // Método para validar si ya existe el usuario
    boolean existsByUsername(String username);
}
