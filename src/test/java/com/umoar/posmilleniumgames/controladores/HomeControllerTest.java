package com.umoar.posmilleniumgames.controladores;

import com.umoar.posmilleniumgames.servicios.ReporteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class HomeControllerTest {

    private final ReporteService reporteService = Mockito.mock(ReporteService.class);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HomeController(reporteService)).build();

    @Test
    void testIndexEndpoint() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
