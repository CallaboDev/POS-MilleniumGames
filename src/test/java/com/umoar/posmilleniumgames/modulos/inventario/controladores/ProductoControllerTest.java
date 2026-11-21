package com.umoar.posmilleniumgames.modulos.inventario.controladores;

import com.umoar.posmilleniumgames.modulos.comun.servicios.FileStorageService;
import com.umoar.posmilleniumgames.modulos.inventario.modelos.Categoria;
import com.umoar.posmilleniumgames.modulos.inventario.modelos.Plataforma;
import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.CategoriaService;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.PlataformaService;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductoControllerTest {

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

        ProductoController controller = new ProductoController(
                productoService, categoriaService, plataformaService, fileStorageService);
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
    void testListarProductosRetornaVistaListaPaginada() throws Exception {
        when(productoService.obtenerPaginado(isNull(), any(Pageable.class)))
                .thenReturn(Page.empty(PageRequest.of(0, ProductoController.CATALOGO_PAGE_SIZE)));

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventario/lista"))
                .andExpect(model().attributeExists("productos"))
                .andExpect(model().attributeExists("productosPage"))
                .andExpect(model().attributeDoesNotExist("buscar"));

        verify(productoService).obtenerPaginado(
                isNull(),
                eq(PageRequest.of(0, 21, Sort.by(Sort.Direction.ASC, "id")))
        );
    }

    @Test
    void testListarProductosConPaginaYBusqueda() throws Exception {
        Producto prod = new Producto();
        prod.setId(22L);
        prod.setSku("PERF-00022");
        prod.setNombre("Zelda");
        Page<Producto> pagina = new PageImpl<>(
                List.of(prod),
                PageRequest.of(1, 21, Sort.by(Sort.Direction.ASC, "id")),
                42
        );

        when(productoService.obtenerPaginado(eq("Zelda"), eq(PageRequest.of(1, 21, Sort.by(Sort.Direction.ASC, "id")))))
                .thenReturn(pagina);

        mockMvc.perform(get("/productos").param("page", "1").param("buscar", "  Zelda  "))
                .andExpect(status().isOk())
                .andExpect(view().name("inventario/lista"))
                .andExpect(model().attribute("buscar", "Zelda"))
                .andExpect(model().attribute("productosPage", pagina));
    }

    @Test
    void testEliminarProductoEliminaEnBdAntesDeEliminarArchivo() throws Exception {
        Producto producto = new Producto();
        producto.setId(5L);
        producto.setImagen("portada.jpg");

        when(productoService.obtenerPorId(5L)).thenReturn(producto);

        mockMvc.perform(get("/productos/eliminar/5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"))
                .andExpect(flash().attributeExists("exito"));

        InOrder orden = inOrder(productoService, fileStorageService);
        orden.verify(productoService).eliminar(5L);
        orden.verify(fileStorageService).eliminarArchivo("portada.jpg");
    }

    @Test
    void testEliminarProductoSiFallaBdNoEliminaArchivo() throws Exception {
        Producto producto = new Producto();
        producto.setId(5L);
        producto.setImagen("portada.jpg");

        when(productoService.obtenerPorId(5L)).thenReturn(producto);
        doThrow(new IllegalStateException("Restricción FK")).when(productoService).eliminar(5L);

        mockMvc.perform(get("/productos/eliminar/5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"))
                .andExpect(flash().attributeExists("error"));

        verify(fileStorageService, never()).eliminarArchivo(anyString());
    }

    @Test
    void testGuardarProductoFallaValidacionLimpiaArchivoSubido() throws Exception {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivoImagen", "nuevo.png", "image/png", "img".getBytes());

        when(fileStorageService.almacenarImagen(any())).thenReturn("uuid-nuevo.png");
        when(productoService.guardar(any(Producto.class))).thenThrow(new IllegalArgumentException("Precio inválido"));
        when(categoriaService.obtenerTodas()).thenReturn(List.of());
        when(plataformaService.obtenerTodas()).thenReturn(List.of());

        mockMvc.perform(multipart("/productos/guardar")
                        .file(archivo)
                        .param("sku", "SKU-1")
                        .param("nombre", "Juego")
                        .param("precio", "-5")
                        .param("stock", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventario/formulario"))
                .andExpect(model().attributeExists("error"));

        verify(fileStorageService).eliminarArchivo("uuid-nuevo.png");
    }

    @Test
    void testGuardarProductoEditadoConNuevaImagenEliminaAnterior() throws Exception {
        Producto existente = new Producto();
        existente.setId(10L);
        existente.setImagen("vieja.jpg");

        MockMultipartFile archivo = new MockMultipartFile(
                "archivoImagen", "nueva.jpg", "image/jpeg", "img".getBytes());

        when(productoService.obtenerPorId(10L)).thenReturn(existente);
        when(fileStorageService.almacenarImagen(any())).thenReturn("nueva-uuid.jpg");
        when(productoService.guardar(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(multipart("/productos/guardar")
                        .file(archivo)
                        .param("id", "10")
                        .param("sku", "SKU-10")
                        .param("nombre", "Zelda")
                        .param("precio", String.valueOf(new BigDecimal("59.99")))
                        .param("stock", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"))
                .andExpect(flash().attributeExists("exito"));

        verify(fileStorageService).eliminarArchivo("vieja.jpg");
    }
}
