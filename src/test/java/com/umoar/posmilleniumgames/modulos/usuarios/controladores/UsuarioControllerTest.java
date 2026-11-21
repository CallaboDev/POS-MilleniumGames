package com.umoar.posmilleniumgames.modulos.usuarios.controladores;

import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Usuario;
import com.umoar.posmilleniumgames.modulos.usuarios.servicios.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioControllerTest {

    private UsuarioService usuarioService;
    private PasswordEncoder passwordEncoder;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        usuarioService = Mockito.mock(UsuarioService.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UsuarioController controller = new UsuarioController(usuarioService, passwordEncoder);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testListarUsuariosPaginadoPorDefecto() throws Exception {
        when(usuarioService.listarUsuariosPaginado(any(Pageable.class)))
                .thenReturn(Page.empty(PageRequest.of(0, UsuarioController.USUARIOS_PAGE_SIZE)));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/lista"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attributeExists("usuariosPage"));

        verify(usuarioService).listarUsuariosPaginado(
                eq(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id")))
        );
    }

    @Test
    void testListarUsuariosSegundaPagina() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(11L);
        usuario.setUsername("cajero11");
        usuario.setNombre("Cajero Once");

        Page<Usuario> pagina = new PageImpl<>(
                List.of(usuario),
                PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "id")),
                22
        );

        when(usuarioService.listarUsuariosPaginado(eq(PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "id")))))
                .thenReturn(pagina);

        mockMvc.perform(get("/usuarios").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/lista"))
                .andExpect(model().attribute("usuariosPage", pagina));
    }
}
