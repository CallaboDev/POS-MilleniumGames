package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.repositorios.ProductoRepository;
import com.umoar.posmilleniumgames.modulos.usuarios.repositorios.UsuarioRepository;
import com.umoar.posmilleniumgames.modulos.ventas.repositorios.ClienteRepository;
import com.umoar.posmilleniumgames.modulos.ventas.repositorios.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReporteServiceImplTest {

    private VentaRepository ventaRepository;
    private ProductoRepository productoRepository;
    private ClienteRepository clienteRepository;
    private UsuarioRepository usuarioRepository;
    private ReporteServiceImpl reporteService;

    @BeforeEach
    void setUp() {
        ventaRepository = Mockito.mock(VentaRepository.class);
        productoRepository = Mockito.mock(ProductoRepository.class);
        clienteRepository = Mockito.mock(ClienteRepository.class);
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        reporteService = new ReporteServiceImpl(
                ventaRepository, productoRepository, clienteRepository, usuarioRepository);
    }

    @Test
    void testConteosDashboard() {
        when(productoRepository.count()).thenReturn(25L);
        when(clienteRepository.count()).thenReturn(14L);
        when(usuarioRepository.count()).thenReturn(4L);

        assertEquals(25L, reporteService.totalVideojuegos());
        assertEquals(14L, reporteService.totalClientes());
        assertEquals(4L, reporteService.totalPersonal());
    }

    @Test
    void testIngresosDelDiaRetornaZeroSiEsNull() {
        LocalDate hoy = LocalDate.of(2026, 11, 20);
        when(ventaRepository.sumarIngresosEntre(hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay()))
                .thenReturn(null);

        BigDecimal ingresos = reporteService.ingresosDelDia(hoy);

        assertEquals(BigDecimal.ZERO, ingresos);
    }

    @Test
    void testIngresosDelDiaRetornaValorCalculado() {
        LocalDate hoy = LocalDate.of(2026, 11, 20);
        when(ventaRepository.sumarIngresosEntre(hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay()))
                .thenReturn(new BigDecimal("199.98"));

        BigDecimal ingresos = reporteService.ingresosDelDia(hoy);

        assertEquals(new BigDecimal("199.98"), ingresos);
    }

    @Test
    void testProductosConStockCriticoPaginado() {
        Pageable pageable = PageRequest.of(0, 15);
        Page<Producto> paginaEsperada = new PageImpl<>(List.of(new Producto()), pageable, 1);
        when(productoRepository.findByStockLessThanEqual(3, pageable)).thenReturn(paginaEsperada);

        Page<Producto> resultado = reporteService.productosConStockCriticoPaginado(pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(productoRepository).findByStockLessThanEqual(3, pageable);
    }

    @Test
    void testProductosMasVendidosPaginado() {
        Pageable pageable = PageRequest.of(0, 15);
        List<Object[]> filas = List.<Object[]>of(new Object[]{"Zelda", 10L, new BigDecimal("599.90")});
        Page<Object[]> paginaEsperada = new PageImpl<>(filas, pageable, 1);
        when(ventaRepository.buscarProductosMasVendidosPaginado(pageable)).thenReturn(paginaEsperada);

        Page<Object[]> resultado = reporteService.productosMasVendidosPaginado(pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(ventaRepository).buscarProductosMasVendidosPaginado(pageable);
    }
}
