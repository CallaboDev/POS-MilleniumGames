package com.umoar.posmilleniumgames.modulos.ventas.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.repositorios.ProductoRepository;
import com.umoar.posmilleniumgames.modulos.usuarios.repositorios.UsuarioRepository;
import com.umoar.posmilleniumgames.modulos.ventas.repositorios.ClienteRepository;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ReporteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class HomeControllerTest {

    private final ReporteService reporteService = Mockito.mock(ReporteService.class);
    private final ProductoRepository productoRepository = Mockito.mock(ProductoRepository.class);
    private final ClienteRepository clienteRepository = Mockito.mock(ClienteRepository.class);
    private final UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HomeController(
            reporteService, productoRepository, clienteRepository, usuarioRepository)).build();

    @Test
    void testIndexEndpoint() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"));
    }
}
