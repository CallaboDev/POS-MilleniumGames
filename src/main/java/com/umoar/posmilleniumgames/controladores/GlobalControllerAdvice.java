package com.umoar.posmilleniumgames.controladores;

import com.umoar.posmilleniumgames.modelos.Usuario;
import com.umoar.posmilleniumgames.repositorios.UsuarioRepository;
import com.umoar.posmilleniumgames.servicios.UsuarioDetalles;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UsuarioRepository usuarioRepository;

    public GlobalControllerAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("nombreUsuarioLogueado")
    public String nombreUsuarioLogueado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "";
        }
        if (authentication.getPrincipal() instanceof UsuarioDetalles detalles) {
            return detalles.getNombre();
        }
        return usuarioRepository.findByUsername(authentication.getName())
                .map(Usuario::getNombre)
                .orElse(authentication.getName());
    }
}
