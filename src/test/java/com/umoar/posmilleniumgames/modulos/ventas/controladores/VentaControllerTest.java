package com.umoar.posmilleniumgames.modulos.ventas.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Usuario;
import com.umoar.posmilleniumgames.modulos.usuarios.repositorios.UsuarioRepository;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Venta;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ClienteService;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VentaControllerTest {

    private VentaService ventaService;
    private ProductoService productoService;
    private ClienteService clienteService;
    private UsuarioRepository usuarioRepository;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ventaService = Mockito.mock(VentaService.class);
        productoService = Mockito.mock(ProductoService.class);
        clienteService = Mockito.mock(ClienteService.class);
        usuarioRepository = Mockito.mock(UsuarioRepository.class);

        VentaController controller = new VentaController(ventaService, productoService, clienteService, usuarioRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testMostrarCaja() throws Exception {
        when(productoService.obtenerTodos()).thenReturn(Collections.emptyList());
        when(clienteService.obtenerTodos()).thenReturn(Collections.emptyList());
        when(clienteService.obtenerOCrearConsumidorFinal()).thenReturn(new Cliente());
        when(ventaService.generarNumeroFactura()).thenReturn("FAC-00001");

        mockMvc.perform(get("/caja"))
                .andExpect(status().isOk())
                .andExpect(view().name("ventas/caja"));
    }

    @Test
    void testMostrarCajaConProductos() throws Exception {
        Producto prod = new Producto();
        prod.setId(10L);
        prod.setSku("PS5-GOW-01");
        prod.setNombre("God of War Ragnarok");
        prod.setPrecio(new BigDecimal("69.99"));
        prod.setStock(15);

        when(productoService.obtenerTodos()).thenReturn(List.of(prod));
        when(clienteService.obtenerTodos()).thenReturn(Collections.emptyList());
        when(clienteService.obtenerOCrearConsumidorFinal()).thenReturn(new Cliente());
        when(ventaService.generarNumeroFactura()).thenReturn("FAC-00001");

        mockMvc.perform(get("/caja"))
                .andExpect(status().isOk())
                .andExpect(view().name("ventas/caja"))
                .andExpect(model().attributeExists("productos"))
                .andExpect(model().attributeExists("proximoNumeroFactura"));
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
}
