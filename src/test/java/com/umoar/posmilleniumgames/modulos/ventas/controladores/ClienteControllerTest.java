package com.umoar.posmilleniumgames.modulos.ventas.controladores;

import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClienteControllerTest {

    private ClienteService clienteService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        clienteService = Mockito.mock(ClienteService.class);
        ClienteController controller = new ClienteController(clienteService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testListarClientesPaginadoPorDefecto() throws Exception {
        when(clienteService.listarPaginado(isNull(), any(Pageable.class)))
                .thenReturn(Page.empty(PageRequest.of(0, ClienteController.CLIENTES_PAGE_SIZE)));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/lista"))
                .andExpect(model().attributeExists("clientes"))
                .andExpect(model().attributeExists("clientesPage"))
                .andExpect(model().attributeDoesNotExist("buscar"));

        verify(clienteService).listarPaginado(
                isNull(),
                eq(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id")))
        );
    }

    @Test
    void testListarClientesConPaginaYBusqueda() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(11L);
        cliente.setNombreCompleto("Carlos Gómez");

        Page<Cliente> pagina = new PageImpl<>(
                List.of(cliente),
                PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "id")),
                25
        );

        when(clienteService.listarPaginado(eq("Carlos"), eq(PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "id")))))
                .thenReturn(pagina);

        mockMvc.perform(get("/clientes").param("page", "1").param("buscar", "  Carlos  "))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/lista"))
                .andExpect(model().attribute("buscar", "Carlos"))
                .andExpect(model().attribute("clientesPage", pagina));
    }
}
