package com.umoar.posmilleniumgames.modulos.inventario.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Plataforma;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.PlataformaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PlataformaControllerTest {

    private PlataformaService plataformaService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        plataformaService = Mockito.mock(PlataformaService.class);
        PlataformaController controller = new PlataformaController(plataformaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGuardarPlataformaForm() throws Exception {
        Plataforma plat = new Plataforma();
        plat.setId(1L);
        plat.setNombre("Steam Deck");

        when(plataformaService.guardar(any(Plataforma.class))).thenReturn(plat);

        mockMvc.perform(post("/plataformas/guardar")
                        .param("nombre", "Steam Deck")
                        .param("redirectUrl", "/productos/nuevo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos/nuevo"))
                .andExpect(flash().attributeExists("exito"));
    }
}
