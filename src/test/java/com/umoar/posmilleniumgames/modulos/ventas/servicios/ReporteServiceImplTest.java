package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.repositorios.ProductoRepository;
import com.umoar.posmilleniumgames.modulos.usuarios.repositorios.UsuarioRepository;
import com.umoar.posmilleniumgames.modulos.ventas.repositorios.ClienteRepository;
import com.umoar.posmilleniumgames.modulos.ventas.repositorios.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
