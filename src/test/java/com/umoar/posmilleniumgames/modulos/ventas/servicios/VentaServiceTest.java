package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Usuario;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Venta;
import com.umoar.posmilleniumgames.modulos.ventas.repositorios.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VentaServiceTest {

    private VentaRepository ventaRepository;
    private ProductoService productoService;
    private ClienteService clienteService;
    private VentaService ventaService;

    @BeforeEach
    void setUp() {
        ventaRepository = Mockito.mock(VentaRepository.class);
        productoService = Mockito.mock(ProductoService.class);
        clienteService = Mockito.mock(ClienteService.class);
        ventaService = new VentaServiceImpl(ventaRepository, productoService, clienteService);
    }

    @Test
    void testProcesarVentaExitosa() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cajero1");

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombreCompleto("Consumidor Final");

        Producto producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Super Mario Odyssey");
        producto.setPrecio(new BigDecimal("59.99"));
        producto.setStock(5);

        when(clienteService.obtenerOCrearConsumidorFinal()).thenReturn(cliente);
        when(productoService.obtenerPorId(10L)).thenReturn(producto);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> {
            Venta v = invocation.getArgument(0);
            v.setId(100L);
            return v;
        });

        RegistroVentaDTO dto = new RegistroVentaDTO();
        dto.setMetodoPago("EFECTIVO");
        dto.getItems().add(new ItemVentaDTO(10L, 2));

        Venta resultado = ventaService.procesarVenta(dto, usuario);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("EFECTIVO", resultado.getMetodoPago());
        assertEquals(new BigDecimal("119.98"), resultado.getTotal());
        assertEquals(1, resultado.getDetalles().size());
        assertEquals(new BigDecimal("59.99"), resultado.getDetalles().get(0).getPrecioUnitario());
        assertEquals(new BigDecimal("119.98"), resultado.getDetalles().get(0).getSubtotal());

        // Verificar que se descontó el stock
        verify(productoService).descontarStock(10L, 2);
        verify(ventaRepository).save(any(Venta.class));
    }

    @Test
    void testProcesarVentaFallaPorStockInsuficiente() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Producto producto = new Producto();
        producto.setId(10L);
        producto.setNombre("The Legend of Zelda");
        producto.setPrecio(new BigDecimal("69.99"));
        producto.setStock(1); // Solo hay 1

        when(clienteService.obtenerOCrearConsumidorFinal()).thenReturn(new Cliente());
        when(productoService.obtenerPorId(10L)).thenReturn(producto);

        RegistroVentaDTO dto = new RegistroVentaDTO();
        dto.getItems().add(new ItemVentaDTO(10L, 3)); // Se piden 3

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            ventaService.procesarVenta(dto, usuario);
        });

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void testProcesarVentaCarritoVacio() {
        Usuario usuario = new Usuario();
        RegistroVentaDTO dto = new RegistroVentaDTO();

        assertThrows(IllegalArgumentException.class, () -> {
            ventaService.procesarVenta(dto, usuario);
        });
    }
}
