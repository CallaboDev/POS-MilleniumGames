package com.umoar.posmilleniumgames.modulos.ventas.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Usuario;
import com.umoar.posmilleniumgames.modulos.usuarios.repositorios.UsuarioRepository;
import com.umoar.posmilleniumgames.modulos.usuarios.servicios.UsuarioDetalles;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Venta;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ClienteService;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.RegistroVentaDTO;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class VentaController {

    private final VentaService ventaService;
    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final UsuarioRepository usuarioRepository;

    public VentaController(VentaService ventaService,
                           ProductoService productoService,
                           ClienteService clienteService,
                           UsuarioRepository usuarioRepository) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Pantalla principal del Punto de Venta (Caja / POS)
     */
    @GetMapping({"/caja", "/ventas/caja"})
    public String mostrarCaja(Model model, HttpSession session, Authentication authentication) {
        Usuario usuario = resolverUsuario(session, authentication);

        model.addAttribute("usuarioActual", usuario);
        model.addAttribute("productos", productoService.obtenerTodos());
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("consumidorFinal", clienteService.obtenerOCrearConsumidorFinal());
        model.addAttribute("proximoNumeroFactura", ventaService.generarNumeroFactura());

        return "ventas/caja";
    }

    /**
     * Endpoint API para búsqueda rápida / escaneo de SKU desde JS
     */
    @GetMapping("/caja/api/productos")
    @ResponseBody
    public List<Producto> apiListarProductos(@RequestParam(required = false) String buscar) {
        if (buscar != null && !buscar.isBlank()) {
            return productoService.buscarPorNombre(buscar);
        }
        return productoService.obtenerTodos();
    }

    /**
     * Endpoint API para procesar venta vía JSON / Fetch desde la interfaz interactiva
     */
    @PostMapping("/caja/procesar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> procesarVentaApi(@RequestBody RegistroVentaDTO registroVentaDTO,
                                                               HttpSession session,
                                                               Authentication authentication) {
        Map<String, Object> respuesta = new HashMap<>();
        try {
            Usuario usuario = resolverUsuario(session, authentication);
            if (usuario == null) {
                respuesta.put("success", false);
                respuesta.put("mensaje", "Debe iniciar sesión para procesar una venta.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
            }

            Venta venta = ventaService.procesarVenta(registroVentaDTO, usuario);

            respuesta.put("success", true);
            respuesta.put("ventaId", venta.getId());
            respuesta.put("numeroFactura", venta.getNumeroFactura());
            respuesta.put("redirectUrl", "/caja/ticket/" + venta.getId());
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException | IllegalStateException e) {
            respuesta.put("success", false);
            respuesta.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        } catch (Exception e) {
            respuesta.put("success", false);
            respuesta.put("mensaje", "Error interno al procesar la venta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    /**
     * Endpoint alternativo para procesamiento tradicional mediante formulario POST
     */
    @PostMapping({"/caja/cobrar", "/ventas/cobrar"})
    public String procesarVentaForm(@ModelAttribute RegistroVentaDTO registroVentaDTO,
                                    HttpSession session,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = resolverUsuario(session, authentication);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para realizar la venta.");
                return "redirect:/login";
            }

            Venta venta = ventaService.procesarVenta(registroVentaDTO, usuario);
            redirectAttributes.addFlashAttribute("exito", "Venta realizada exitosamente. Factura: " + venta.getNumeroFactura());
            return "redirect:/caja/ticket/" + venta.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/caja";
        }
    }

    /**
     * Vista limpia de ticket / comprobante para impresión
     */
    @GetMapping({"/caja/ticket/{id}", "/ventas/ticket/{id}"})
    public String verTicket(@PathVariable Long id, Model model) {
        try {
            Venta venta = ventaService.obtenerPorId(id);
            model.addAttribute("venta", venta);
            return "ventas/ticket";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/caja";
        }
    }

    /**
     * Resuelve el usuario de la sesión (session.getAttribute("usuarioLogueado")),
     * o lo sincroniza desde Authentication de Spring Security.
     */
    private Usuario resolverUsuario(HttpSession session, Authentication authentication) {
        Object usuarioEnSesion = session.getAttribute("usuarioLogueado");
        if (usuarioEnSesion instanceof Usuario usuario) {
            return usuario;
        }

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Usuario usuario = null;
            if (authentication.getPrincipal() instanceof UsuarioDetalles detalles) {
                usuario = detalles.getUsuario();
            } else {
                usuario = usuarioRepository.findByUsername(authentication.getName()).orElse(null);
            }

            if (usuario != null) {
                session.setAttribute("usuarioLogueado", usuario);
                return usuario;
            }
        }
        return null;
    }
}
