package com.umoar.posmilleniumgames.modulos.inventario.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.repositorios.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoServiceImplTest {

    private ProductoRepository productoRepository;
    private ProductoServiceImpl productoService;

    @BeforeEach
    void setUp() {
        productoRepository = Mockito.mock(ProductoRepository.class);
        productoService = new ProductoServiceImpl(productoRepository);
    }

    @Test
    void testObtenerPorIdExistente() {
        Producto producto = new Producto();
        producto.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.obtenerPorId(1L);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void testObtenerPorIdNoExistenteLanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productoService.obtenerPorId(99L));
    }

    @Test
    void testBuscarPorNombreVacioRetornaTodos() {
        when(productoRepository.findAll()).thenReturn(List.of(new Producto()));

        List<Producto> resultado = productoService.buscarPorNombre("   ");

        assertEquals(1, resultado.size());
        verify(productoRepository).findAll();
        verify(productoRepository, never()).findByNombreContainingIgnoreCase(anyString());
    }

    @Test
    void testGuardarValido() {
        Producto producto = new Producto();
        producto.setPrecio(new BigDecimal("49.99"));
        producto.setStock(10);
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto guardado = productoService.guardar(producto);

        assertSame(producto, guardado);
        verify(productoRepository).save(producto);
    }

    @Test
    void testGuardarConPrecioNegativoLanzaExcepcion() {
        Producto producto = new Producto();
        producto.setPrecio(new BigDecimal("-1.00"));
        producto.setStock(5);

        assertThrows(IllegalArgumentException.class, () -> productoService.guardar(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testGuardarConStockNegativoLanzaExcepcion() {
        Producto producto = new Producto();
        producto.setPrecio(new BigDecimal("10.00"));
        producto.setStock(-1);

        assertThrows(IllegalArgumentException.class, () -> productoService.guardar(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testEliminarExistente() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.eliminar(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    void testEliminarNoExistenteLanzaExcepcion() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> productoService.eliminar(99L));
        verify(productoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDescontarStockExitoso() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Zelda");
        producto.setStock(5);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        productoService.descontarStock(1L, 3);

        assertEquals(2, producto.getStock());
        verify(productoRepository).save(producto);
    }

    @Test
    void testDescontarStockInsuficienteLanzaExcepcion() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Zelda");
        producto.setStock(1);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(IllegalStateException.class, () -> productoService.descontarStock(1L, 3));
        verify(productoRepository, never()).save(any());
    }
}
