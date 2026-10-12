package com.umoar.posmilleniumgames.modulos.inventario.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Categoria;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoriaControllerTest {

    private CategoriaService categoriaService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        categoriaService = Mockito.mock(CategoriaService.class);
        CategoriaController controller = new CategoriaController(categoriaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGuardarCategoriaForm() throws Exception {
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Coleccionables");

        when(categoriaService.guardar(any(Categoria.class))).thenReturn(cat);

        mockMvc.perform(post("/categorias/guardar")
                        .param("nombre", "Coleccionables")
                        .param("redirectUrl", "/productos/nuevo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos/nuevo"))
                .andExpect(flash().attributeExists("exito"));
    }
}
