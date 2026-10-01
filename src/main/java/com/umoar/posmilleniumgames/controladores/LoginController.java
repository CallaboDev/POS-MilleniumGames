package com.umoar.posmilleniumgames.controladores;

import com.umoar.posmilleniumgames.modelos.Usuario;
import com.umoar.posmilleniumgames.servicios.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/autenticar")
    public String autenticar(@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.buscarPorUsername(username)
                .filter(usuarioEncontrado -> usuarioEncontrado.getPassword() != null
                        && usuarioEncontrado.getPassword().equals(password))
                .orElse(null);

        if (usuario != null) {
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos.");
        return "redirect:/login";
    }


    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}


