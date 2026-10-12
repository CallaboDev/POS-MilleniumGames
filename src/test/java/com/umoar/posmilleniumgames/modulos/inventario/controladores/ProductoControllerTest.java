package com.umoar.posmilleniumgames.modulos.inventario.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Categoria;
import com.umoar.posmilleniumgames.modulos.inventario.modelos.Plataforma;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.CategoriaService;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.PlataformaService;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductoControllerTest {

    private ProductoService productoService;
    private CategoriaService categoriaService;
    private PlataformaService plataformaService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        productoService = Mockito.mock(ProductoService.class);
        categoriaService = Mockito.mock(CategoriaService.class);
        plataformaService = Mockito.mock(PlataformaService.class);

        ProductoController controller = new ProductoController(productoService, categoriaService, plataformaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testNuevoProductoFormularioRetornaVistaCorrecta() throws Exception {
        when(categoriaService.obtenerTodas()).thenReturn(List.of(new Categoria()));
        when(plataformaService.obtenerTodas()).thenReturn(List.of(new Plataforma()));

        mockMvc.perform(get("/productos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventario/formulario"))
                .andExpect(model().attributeExists("producto"))
                .andExpect(model().attributeExists("categorias"))
                .andExpect(model().attributeExists("plataformas"));
    }

    @Test
    void testListarProductosRetornaVistaLista() throws Exception {
        when(productoService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventario/lista"))
                .andExpect(model().attributeExists("productos"));
    }
}
