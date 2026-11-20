package com.umoar.posmilleniumgames.modulos.inventario.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Categoria;
import com.umoar.posmilleniumgames.modulos.inventario.repositorios.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoriaServiceImplTest {

    private CategoriaRepository categoriaRepository;
    private CategoriaServiceImpl categoriaService;

    @BeforeEach
    void setUp() {
        categoriaRepository = Mockito.mock(CategoriaRepository.class);
        categoriaService = new CategoriaServiceImpl(categoriaRepository);
    }

    @Test
    void testGuardarValida() {
        Categoria categoria = new Categoria();
        categoria.setNombre("  Accesorios  ");
        when(categoriaRepository.existsByNombreIgnoreCase("Accesorios")).thenReturn(false);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria guardada = categoriaService.guardar(categoria);

        assertEquals("Accesorios", guardada.getNombre());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void testGuardarDuplicadaLanzaExcepcion() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Videojuegos");
        when(categoriaRepository.existsByNombreIgnoreCase("Videojuegos")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> categoriaService.guardar(categoria));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void testCrearSiNoExisteCreaNueva() {
        when(categoriaRepository.findByNombreIgnoreCase("Consolas")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        Categoria resultado = categoriaService.crearSiNoExiste(" Consolas ");

        assertNotNull(resultado);
        assertEquals("Consolas", resultado.getNombre());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void testCrearSiNoExisteRetornaExistente() {
        Categoria existente = new Categoria();
        existente.setId(5L);
        existente.setNombre("Consolas");
        when(categoriaRepository.findByNombreIgnoreCase("Consolas")).thenReturn(Optional.of(existente));

        Categoria resultado = categoriaService.crearSiNoExiste("Consolas");

        assertSame(existente, resultado);
        verify(categoriaRepository, never()).save(any());
    }
}
