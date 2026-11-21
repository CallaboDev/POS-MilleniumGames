package com.umoar.posmilleniumgames.modulos.ventas.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.dto.ProductoResponseDTO;
import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Usuario;
import com.umoar.posmilleniumgames.modulos.usuarios.servicios.UsuarioDetalles;
import com.umoar.posmilleniumgames.modulos.usuarios.servicios.UsuarioService;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Venta;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ClienteService;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.RegistroVentaDTO;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public static final int CAJA_PAGE_SIZE = 21;

    private final VentaService ventaService;
    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    public VentaController(VentaService ventaService,
                           ProductoService productoService,
                           ClienteService clienteService,
                           UsuarioService usuarioService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    /**
     * Pantalla principal del Punto de Venta (Caja / POS)
     */
    @GetMapping({"/caja", "/ventas/caja"})
    public String mostrarCaja(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) String buscar,
                              Model model,
                              HttpSession session,
                              Authentication authentication) {
        Usuario usuario = resolverUsuario(session, authentication);
        Page<Producto> paginaProductos = obtenerPaginaCaja(buscar, page, CAJA_PAGE_SIZE);

        model.addAttribute("usuarioActual", usuario);
        model.addAttribute("productosPage", paginaProductos);
        model.addAttribute("productos", paginaProductos.getContent());
        model.addAttribute("buscar", buscar != null ? buscar.trim() : "");
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("consumidorFinal", clienteService.obtenerOCrearConsumidorFinal());
        model.addAttribute("proximoNumeroFactura", ventaService.generarNumeroFactura());

        return "ventas/caja";
    }

    /**
     * Endpoint API paginado para búsqueda y navegación rápida en caja sin perder el carrito
     */
    @GetMapping("/caja/api/productos")
    @ResponseBody
    public PaginaProductosCajaDTO apiListarProductos(
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size) {
        int tamano = (size > 0 && size <= 100) ? size : CAJA_PAGE_SIZE;
        Page<Producto> pagina = obtenerPaginaCaja(buscar, page, tamano);

        List<ProductoResponseDTO> contenido = pagina.getContent().stream()
                .map(ProductoResponseDTO::fromEntity)
                .toList();

        return new PaginaProductosCajaDTO(
                contenido,
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isFirst(),
                pagina.isLast()
        );
    }

    /**
     * Endpoint API para escaneo exacto de SKU / código de barras
     */
    @GetMapping("/caja/api/productos/sku")
    @ResponseBody
    public ResponseEntity<ProductoResponseDTO> apiBuscarPorSku(@RequestParam String sku) {
        Producto producto = productoService.buscarPorSkuIgnoreCase(sku);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(ProductoResponseDTO.fromEntity(producto));
    }

    private Page<Producto> obtenerPaginaCaja(String buscar, int page, int size) {
        int paginaSegura = Math.max(0, page);
        Pageable pageable = PageRequest.of(paginaSegura, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Producto> pagina = productoService.obtenerPaginado(buscar, pageable);
        if (paginaSegura > 0 && pagina.getTotalPages() > 0 && paginaSegura >= pagina.getTotalPages()) {
            Pageable ultimaPagina = PageRequest.of(pagina.getTotalPages() - 1, size, Sort.by(Sort.Direction.ASC, "id"));
            pagina = productoService.obtenerPaginado(buscar, ultimaPagina);
        }
        return pagina;
    }

    public record PaginaProductosCajaDTO(
            List<ProductoResponseDTO> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {
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
                usuario = usuarioService.buscarPorUsername(authentication.getName()).orElse(null);
            }

            if (usuario != null) {
                session.setAttribute("usuarioLogueado", usuario);
                return usuario;
            }
        }
        return null;
    }
}
