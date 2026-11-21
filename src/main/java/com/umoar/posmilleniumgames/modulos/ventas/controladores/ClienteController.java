package com.umoar.posmilleniumgames.modulos.ventas.controladores;

import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    public static final int CLIENTES_PAGE_SIZE = 10;

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) { this.clienteService = clienteService; }

    @GetMapping
    public String listar(@RequestParam(value = "buscar", required = false) String buscar,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         Model model) {
        String terminoLimpio = (buscar != null && !buscar.trim().isEmpty()) ? buscar.trim() : null;
        int paginaSegura = Math.max(0, page);
        Pageable pageable = PageRequest.of(paginaSegura, CLIENTES_PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id"));
        Page<Cliente> paginaClientes = clienteService.listarPaginado(terminoLimpio, pageable);

        if (paginaSegura > 0 && paginaClientes.getTotalPages() > 0 && paginaSegura >= paginaClientes.getTotalPages()) {
            Pageable ultimaPagina = PageRequest.of(
                    paginaClientes.getTotalPages() - 1,
                    CLIENTES_PAGE_SIZE,
                    Sort.by(Sort.Direction.ASC, "id")
            );
            paginaClientes = clienteService.listarPaginado(terminoLimpio, ultimaPagina);
        }

        model.addAttribute("clientesPage", paginaClientes);
        model.addAttribute("clientes", paginaClientes.getContent());
        if (terminoLimpio != null) {
            model.addAttribute("buscar", terminoLimpio);
        }
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
