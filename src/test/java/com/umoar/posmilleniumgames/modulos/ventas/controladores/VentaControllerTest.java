package com.umoar.posmilleniumgames.modulos.ventas.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Plataforma;
import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import com.umoar.posmilleniumgames.modulos.usuarios.servicios.UsuarioService;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Venta;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ClienteService;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VentaControllerTest {

    private VentaService ventaService;
    private ProductoService productoService;
    private ClienteService clienteService;
    private UsuarioService usuarioService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ventaService = Mockito.mock(VentaService.class);
        productoService = Mockito.mock(ProductoService.class);
        clienteService = Mockito.mock(ClienteService.class);
        usuarioService = Mockito.mock(UsuarioService.class);

        VentaController controller = new VentaController(ventaService, productoService, clienteService, usuarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testMostrarCaja() throws Exception {
        when(productoService.obtenerPaginado(isNull(), any(Pageable.class)))
                .thenReturn(Page.empty(PageRequest.of(0, VentaController.CAJA_PAGE_SIZE)));
        when(clienteService.obtenerTodos()).thenReturn(Collections.emptyList());
        when(clienteService.obtenerOCrearConsumidorFinal()).thenReturn(new Cliente());
        when(ventaService.generarNumeroFactura()).thenReturn("FAC-00001");

        mockMvc.perform(get("/caja"))
                .andExpect(status().isOk())
                .andExpect(view().name("ventas/caja"))
                .andExpect(model().attributeExists("productosPage"));

        verify(productoService).obtenerPaginado(
                isNull(),
                eq(PageRequest.of(0, 21, Sort.by(Sort.Direction.ASC, "id")))
        );
    }

    @Test
    void testMostrarCajaConProductos() throws Exception {
        Producto prod = crearProducto(10L, "PS5-GOW-01", "God of War Ragnarok", "PlayStation 5");
        Page<Producto> pagina = new PageImpl<>(
                List.of(prod),
                PageRequest.of(0, VentaController.CAJA_PAGE_SIZE),
                42
        );

        when(productoService.obtenerPaginado(isNull(), any(Pageable.class))).thenReturn(pagina);
        when(clienteService.obtenerTodos()).thenReturn(Collections.emptyList());
        when(clienteService.obtenerOCrearConsumidorFinal()).thenReturn(new Cliente());
        when(ventaService.generarNumeroFactura()).thenReturn("FAC-00001");

        mockMvc.perform(get("/caja"))
                .andExpect(status().isOk())
                .andExpect(view().name("ventas/caja"))
                .andExpect(model().attributeExists("productos"))
                .andExpect(model().attributeExists("productosPage"))
                .andExpect(model().attributeExists("proximoNumeroFactura"));
    }

    @Test
    void testApiListarProductosPaginado() throws Exception {
        Producto prod = crearProducto(1L, "PERF-00001", "Producto Prueba #1", "PC Gaming");
        Page<Producto> pagina = new PageImpl<>(
                List.of(prod),
                PageRequest.of(1, 21, Sort.by(Sort.Direction.ASC, "id")),
                4000
        );

        when(productoService.obtenerPaginado(eq("Prueba"), eq(PageRequest.of(1, 21, Sort.by(Sort.Direction.ASC, "id")))))
                .thenReturn(pagina);

        mockMvc.perform(get("/caja/api/productos")
                        .param("page", "1")
                        .param("size", "21")
                        .param("buscar", "Prueba"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(21))
                .andExpect(jsonPath("$.totalElements").value(4000))
                .andExpect(jsonPath("$.totalPages").value(191))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].sku").value("PERF-00001"))
                .andExpect(jsonPath("$.content[0].plataforma").value("PC Gaming"));
    }

    @Test
    void testApiBuscarPorSkuEncontradoYNoEncontrado() throws Exception {
        Producto prod = crearProducto(5L, "PERF-03500", "Juego Escaneado", "Nintendo Switch");
        when(productoService.buscarPorSkuIgnoreCase("perf-03500")).thenReturn(prod);
        when(productoService.buscarPorSkuIgnoreCase("NO-EXISTE")).thenReturn(null);

        mockMvc.perform(get("/caja/api/productos/sku").param("sku", "perf-03500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.sku").value("PERF-03500"));

        mockMvc.perform(get("/caja/api/productos/sku").param("sku", "NO-EXISTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testVerTicket() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setNumeroFactura("FAC-00001");
        venta.setFechaHora(LocalDateTime.now());
        venta.setTotal(new BigDecimal("59.99"));
        venta.setMetodoPago("EFECTIVO");

        when(ventaService.obtenerPorId(1L)).thenReturn(venta);

        mockMvc.perform(get("/caja/ticket/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ventas/ticket"))
                .andExpect(model().attributeExists("venta"));
    }

    private Producto crearProducto(Long id, String sku, String nombre, String nombrePlataforma) {
        Plataforma plataforma = new Plataforma();
        plataforma.setId(1L);
        plataforma.setNombre(nombrePlataforma);

        Producto prod = new Producto();
        prod.setId(id);
        prod.setSku(sku);
        prod.setNombre(nombre);
        prod.setPrecio(new BigDecimal("69.99"));
        prod.setStock(15);
        prod.setPlataforma(plataforma);
        return prod;
    }
}
