package com.umoar.posmilleniumgames.configuraciones;

import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Usuario;
import com.umoar.posmilleniumgames.modulos.usuarios.servicios.UsuarioDetalles;
import com.umoar.posmilleniumgames.modulos.usuarios.servicios.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UsuarioService usuarioService;

    public GlobalControllerAdvice(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("nombreUsuarioLogueado")
    public String nombreUsuarioLogueado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "";
        }
        if (authentication.getPrincipal() instanceof UsuarioDetalles detalles) {
            return detalles.getNombre();
        }
        return usuarioService.buscarPorUsername(authentication.getName())
                .map(Usuario::getNombre)
                .orElse(authentication.getName());
    }
}
