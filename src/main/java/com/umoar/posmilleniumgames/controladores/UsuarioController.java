package com.umoar.posmilleniumgames.controladores;

import com.umoar.posmilleniumgames.modelos.Usuario;
import com.umoar.posmilleniumgames.servicios.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        prepararFormulario(model, new Usuario(), false);
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario, Model model) {
        String error = validarUsuario(usuario, true);
        if (error != null) {
            model.addAttribute("error", error);
            prepararFormulario(model, usuario, false);
            return "usuarios/formulario";
        }

        if (usuarioService.existePorUsername(usuario.getUsername().trim())) {
            model.addAttribute("error", "Ese username ya está registrado.");
            prepararFormulario(model, usuario, false);
            return "usuarios/formulario";
        }

        usuario.setUsername(usuario.getUsername().trim());
        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.buscarPorId(id).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró el empleado solicitado.");
            return "redirect:/usuarios";
        }

        prepararFormulario(model, usuario, true);
        return "usuarios/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Usuario usuario,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Usuario usuarioActual = usuarioService.buscarPorId(id).orElse(null);
        if (usuarioActual == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró el empleado solicitado.");
            return "redirect:/usuarios";
        }

        usuario.setId(id);
        String error = validarUsuario(usuario, false);
        if (error != null) {
            model.addAttribute("error", error);
            prepararFormulario(model, usuario, true);
            return "usuarios/formulario";
        }

        String username = usuario.getUsername().trim();
        boolean usernameEnUso = usuarioService.buscarPorUsername(username)
                .filter(otroUsuario -> !Objects.equals(otroUsuario.getId(), id))
                .isPresent();
        if (usernameEnUso) {
            model.addAttribute("error", "Ese username ya está registrado.");
            prepararFormulario(model, usuario, true);
            return "usuarios/formulario";
        }

        usuario.setUsername(username);
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            usuario.setPassword(usuarioActual.getPassword());
        }

        usuarioService.actualizar(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Empleado eliminado.");
        return "redirect:/usuarios";
    }

    private void prepararFormulario(Model model, Usuario usuario, boolean esEdicion) {
        model.addAttribute("usuario", usuario);
        model.addAttribute("esEdicion", esEdicion);
    }

    private String validarUsuario(Usuario usuario, boolean requierePassword) {
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            return "El username es obligatorio.";
        }
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            return "El nombre es obligatorio.";
        }
        if (requierePassword && (usuario.getPassword() == null || usuario.getPassword().isBlank())) {
            return "La contraseña es obligatoria para crear un empleado.";
        }
        if (usuario.getRol() == null
                || !("ADMIN".equalsIgnoreCase(usuario.getRol())
                || "CAJERO".equalsIgnoreCase(usuario.getRol()))) {
            return "Selecciona un rol válido.";
        }
        usuario.setRol(usuario.getRol().trim().toUpperCase());
        return null;
    }
}
