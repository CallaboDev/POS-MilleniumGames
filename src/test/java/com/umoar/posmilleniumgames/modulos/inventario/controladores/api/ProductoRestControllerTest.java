package com.umoar.posmilleniumgames.modulos.inventario.controladores.api;

import com.umoar.posmilleniumgames.modulos.comun.servicios.FileStorageService;
import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.CategoriaService;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.PlataformaService;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductoRestControllerTest {

    private ProductoService productoService;
    private CategoriaService categoriaService;
    private PlataformaService plataformaService;
    private FileStorageService fileStorageService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        productoService = Mockito.mock(ProductoService.class);
        categoriaService = Mockito.mock(CategoriaService.class);
        plataformaService = Mockito.mock(PlataformaService.class);
        fileStorageService = Mockito.mock(FileStorageService.class);

        ProductoRestController controller = new ProductoRestController(
                productoService, categoriaService, plataformaService, fileStorageService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testEliminarProductoEliminaEnBdYDespuesArchivo() throws Exception {
        Producto producto = new Producto();
        producto.setId(3L);
        producto.setImagen("cover.png");
        when(productoService.obtenerPorId(3L)).thenReturn(producto);

        mockMvc.perform(delete("/api/v1/productos/3"))
                .andExpect(status().isNoContent());

        InOrder orden = inOrder(productoService, fileStorageService);
        orden.verify(productoService).eliminar(3L);
        orden.verify(fileStorageService).eliminarArchivo("cover.png");
    }

    @Test
    void testEliminarProductoNoExistenteRetorna404() throws Exception {
        when(productoService.obtenerPorId(99L)).thenThrow(new IllegalArgumentException("No encontrado"));

        mockMvc.perform(delete("/api/v1/productos/99"))
                .andExpect(status().isNotFound());

        verify(fileStorageService, never()).eliminarArchivo(anyString());
    }
}
