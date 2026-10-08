package com.umoar.posmilleniumgames.controladores;

import com.umoar.posmilleniumgames.modelos.Rol;
import com.umoar.posmilleniumgames.modelos.Usuario;
import com.umoar.posmilleniumgames.repositorios.RolRepository;
import com.umoar.posmilleniumgames.servicios.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        prepararFormulario(model, new UsuarioForm(), false);
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuario") UsuarioForm form, Model model) {
        String error = validarForm(form, true);
        if (error != null) {
            model.addAttribute("error", error);
            prepararFormulario(model, form, false);
            return "usuarios/formulario";
        }

        if (usuarioService.existePorUsername(form.getUsername().trim())) {
            model.addAttribute("error", "Ese username ya est registrado.");
            prepararFormulario(model, form, false);
            return "usuarios/formulario";
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(form.getUsername().trim());
        usuario.setNombre(form.getNombre().trim());
        usuario.setPassword(passwordEncoder.encode(form.getPassword()));
        usuario.setEnabled(true);

        Optional<Rol> rolOpt = rolRepository.findByNombre("ROLE_" + form.getRol().toUpperCase());
        rolOpt.ifPresent(rol -> usuario.getRoles().add(rol));

        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.buscarPorId(id).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontr el empleado solicitado.");
            return "redirect:/usuarios";
        }

        UsuarioForm form = new UsuarioForm();
        form.setId(usuario.getId());
        form.setUsername(usuario.getUsername());
        form.setNombre(usuario.getNombre());
        if (!usuario.getRoles().isEmpty()) {
            String roleName = usuario.getRoles().iterator().next().getNombre().replace("ROLE_", "");
            form.setRol(roleName);
        }

        prepararFormulario(model, form, true);
        return "usuarios/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("usuario") UsuarioForm form,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Usuario usuarioActual = usuarioService.buscarPorId(id).orElse(null);
        if (usuarioActual == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontr el empleado solicitado.");
            return "redirect:/usuarios";
        }

        String error = validarForm(form, false);
        if (error != null) {
            model.addAttribute("error", error);
            prepararFormulario(model, form, true);
            return "usuarios/formulario";
        }

        String username = form.getUsername().trim();
        boolean usernameEnUso = usuarioService.buscarPorUsername(username)
                .filter(otroUsuario -> !Objects.equals(otroUsuario.getId(), id))
                .isPresent();
        if (usernameEnUso) {
            model.addAttribute("error", "Ese username ya est registrado.");
            prepararFormulario(model, form, true);
            return "usuarios/formulario";
        }

        usuarioActual.setUsername(username);
        usuarioActual.setNombre(form.getNombre().trim());
        
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            usuarioActual.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        Optional<Rol> rolOpt = rolRepository.findByNombre("ROLE_" + form.getRol().toUpperCase());
        if (rolOpt.isPresent()) {
            usuarioActual.getRoles().clear();
            usuarioActual.getRoles().add(rolOpt.get());
        }

        usuarioService.actualizar(usuarioActual);
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Empleado eliminado.");
        return "redirect:/usuarios";
    }

    private void prepararFormulario(Model model, UsuarioForm form, boolean esEdicion) {
        model.addAttribute("usuario", form);
        model.addAttribute("esEdicion", esEdicion);
    }

    private String validarForm(UsuarioForm form, boolean requierePassword) {
        if (form.getUsername() == null || form.getUsername().isBlank()) {
            return "El username es obligatorio.";
        }
        if (form.getNombre() == null || form.getNombre().isBlank()) {
            return "El nombre es obligatorio.";
        }
        if (requierePassword && (form.getPassword() == null || form.getPassword().isBlank())) {
            return "La contrasea es obligatoria para crear un empleado.";
        }
        if (form.getRol() == null
                || !("ADMIN".equalsIgnoreCase(form.getRol())
                || "CAJERO".equalsIgnoreCase(form.getRol()))) {
            return "Selecciona un rol vlido.";
        }
        form.setRol(form.getRol().trim().toUpperCase());
        return null;
    }

    public static class UsuarioForm {
        private Long id;
        private String username;
        private String nombre;
        private String password;
        private String rol;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }
}
