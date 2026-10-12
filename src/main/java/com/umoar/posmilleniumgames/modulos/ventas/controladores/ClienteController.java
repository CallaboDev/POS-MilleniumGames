package com.umoar.posmilleniumgames.modulos.ventas.controladores;

import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) { this.clienteService = clienteService; }

    @GetMapping
    public String listar(@RequestParam(value = "buscar", required = false) String buscar, Model model) {
        model.addAttribute("clientes", clienteService.listar(buscar));
        model.addAttribute("buscar", buscar);
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("modoEdicion", false);
        return "clientes/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes attrs) {
        return clienteService.buscarPorId(id).map(cliente -> {
            model.addAttribute("cliente", cliente);
            model.addAttribute("modoEdicion", true);
            return "clientes/formulario";
        }).orElseGet(() -> {
            attrs.addFlashAttribute("error", "No se encontró el cliente.");
            return "redirect:/clientes";
        });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente, RedirectAttributes attrs) {
        try {
            if (cliente.getId() == null) clienteService.guardar(cliente);
            else clienteService.actualizar(cliente.getId(), cliente);
            attrs.addFlashAttribute("exito", "Cliente guardado correctamente.");
            return "redirect:/clientes";
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            return cliente.getId() == null ? "redirect:/clientes/nuevo" : "redirect:/clientes/editar/" + cliente.getId();
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes attrs) {
        try {
            clienteService.eliminar(id);
            attrs.addFlashAttribute("exito", "Cliente eliminado correctamente.");
        } catch (IllegalArgumentException e) { attrs.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/clientes";
    }
}
