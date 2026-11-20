package com.umoar.posmilleniumgames.modulos.inventario.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Plataforma;
import com.umoar.posmilleniumgames.modulos.inventario.repositorios.PlataformaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlataformaServiceImplTest {

    private PlataformaRepository plataformaRepository;
    private PlataformaServiceImpl plataformaService;

    @BeforeEach
    void setUp() {
        plataformaRepository = Mockito.mock(PlataformaRepository.class);
        plataformaService = new PlataformaServiceImpl(plataformaRepository);
    }

    @Test
    void testGuardarValida() {
        Plataforma plataforma = new Plataforma();
        plataforma.setNombre("  PlayStation 5  ");
        when(plataformaRepository.existsByNombreIgnoreCase("PlayStation 5")).thenReturn(false);
        when(plataformaRepository.save(plataforma)).thenReturn(plataforma);

        Plataforma guardada = plataformaService.guardar(plataforma);

        assertEquals("PlayStation 5", guardada.getNombre());
        verify(plataformaRepository).save(plataforma);
    }

    @Test
    void testGuardarDuplicadaLanzaExcepcion() {
        Plataforma plataforma = new Plataforma();
        plataforma.setNombre("Nintendo Switch");
        when(plataformaRepository.existsByNombreIgnoreCase("Nintendo Switch")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> plataformaService.guardar(plataforma));
        verify(plataformaRepository, never()).save(any());
    }

    @Test
    void testCrearSiNoExisteBlancoRetornaNull() {
        assertNull(plataformaService.crearSiNoExiste("   "));
        verifyNoInteractions(plataformaRepository);
    }

    @Test
    void testCrearSiNoExisteRetornaExistente() {
        Plataforma existente = new Plataforma();
        existente.setId(2L);
        existente.setNombre("PC");
        when(plataformaRepository.findByNombreIgnoreCase("PC")).thenReturn(Optional.of(existente));

        Plataforma resultado = plataformaService.crearSiNoExiste("PC");

        assertSame(existente, resultado);
        verify(plataformaRepository, never()).save(any());
    }
}
