package com.umoar.posmilleniumgames.modulos.ventas.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ReporteService;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HomeControllerTest {

    private final ReporteService reporteService = Mockito.mock(ReporteService.class);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HomeController(reporteService)).build();

    @Test
    void testIndexEndpoint() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"));
    }

    @Test
    void testReportesEndpointConPaginacionDe15() throws Exception {
        when(reporteService.buscarVentas(any(), any())).thenReturn(Collections.emptyList());
        when(reporteService.ingresosEntre(any(), any())).thenReturn(BigDecimal.ZERO);
        when(reporteService.ventasEntre(any(), any())).thenReturn(0L);
        when(reporteService.productosMasVendidosPaginado(any(Pageable.class)))
                .thenReturn(Page.empty(PageRequest.of(0, HomeController.REPORTES_PAGE_SIZE)));
        when(reporteService.productosConStockCriticoPaginado(any(Pageable.class)))
                .thenReturn(Page.empty(PageRequest.of(0, HomeController.REPORTES_PAGE_SIZE)));

        mockMvc.perform(get("/reportes"))
                .andExpect(status().isOk())
                .andExpect(view().name("ventas/reportes"))
                .andExpect(model().attributeExists("productosMasVendidosPage"))
                .andExpect(model().attributeExists("stockCriticoPage"));

        verify(reporteService).productosMasVendidosPaginado(eq(PageRequest.of(0, 15)));
        verify(reporteService).productosConStockCriticoPaginado(
                eq(PageRequest.of(0, 15, Sort.by(Sort.Order.asc("stock"), Sort.Order.asc("id"))))
        );
    }

    @Test
    void testApiMasVendidosPaginado() throws Exception {
        List<Object[]> filas = List.<Object[]>of(new Object[]{"Zelda TOTK", 12L, new BigDecimal("839.88")});
        Page<Object[]> pagina = new PageImpl<>(filas, PageRequest.of(1, 15), 35);
        when(reporteService.productosMasVendidosPaginado(eq(PageRequest.of(1, 15)))).thenReturn(pagina);

        mockMvc.perform(get("/reportes/api/mas-vendidos").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.totalElements").value(35))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.content[0].nombre").value("Zelda TOTK"))
                .andExpect(jsonPath("$.content[0].unidades").value(12))
                .andExpect(jsonPath("$.content[0].ingresos").value(839.88));
    }

    @Test
    void testApiStockCriticoPaginado() throws Exception {
        Producto prod = new Producto();
        prod.setId(5L);
        prod.setNombre("Control DualSense");
        prod.setSku("ACC-DS-01");
        prod.setStock(2);

        Pageable esperado = PageRequest.of(1, 15, Sort.by(Sort.Order.asc("stock"), Sort.Order.asc("id")));
        Page<Producto> pagina = new PageImpl<>(List.of(prod), esperado, 40);
        when(reporteService.productosConStockCriticoPaginado(eq(esperado))).thenReturn(pagina);

        mockMvc.perform(get("/reportes/api/stock-critico").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.totalElements").value(40))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.content[0].nombre").value("Control DualSense"))
                .andExpect(jsonPath("$.content[0].sku").value("ACC-DS-01"))
                .andExpect(jsonPath("$.content[0].stock").value(2));
    }
}
