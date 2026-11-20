package com.umoar.posmilleniumgames.modulos.comun.servicios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private FileStorageServiceImpl fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageServiceImpl(tempDir);
    }

    @Test
    void testAlmacenarImagenValida() throws Exception {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivoImagen",
                "portada.png",
                "image/png",
                "contenido-imagen".getBytes()
        );

        String nombreGuardado = fileStorageService.almacenarImagen(archivo);

        assertNotNull(nombreGuardado);
        assertTrue(nombreGuardado.endsWith(".png"));
        Path rutaGuardada = tempDir.resolve(nombreGuardado);
        assertTrue(Files.exists(rutaGuardada));
        assertArrayEquals("contenido-imagen".getBytes(), Files.readAllBytes(rutaGuardada));
    }

    @Test
    void testAlmacenarImagenConRutaEnNombreOriginalExtraeSoloExtension() {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivoImagen",
                "../../hack/cover.webp",
                "image/webp",
                "webp-data".getBytes()
        );

        String nombreGuardado = fileStorageService.almacenarImagen(archivo);

        assertTrue(nombreGuardado.endsWith(".webp"));
        assertFalse(nombreGuardado.contains("/"));
        assertFalse(nombreGuardado.contains(".."));
        assertTrue(Files.exists(tempDir.resolve(nombreGuardado)));
    }

    @Test
    void testAlmacenarImagenVaciaLanzaExcepcion() {
        MockMultipartFile archivoVacio = new MockMultipartFile(
                "archivoImagen",
                "vacio.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> fileStorageService.almacenarImagen(archivoVacio));
    }

    @Test
    void testAlmacenarImagenTipoInvalidoLanzaExcepcion() {
        MockMultipartFile archivoTexto = new MockMultipartFile(
                "archivoImagen",
                "documento.txt",
                "text/plain",
                "hola".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> fileStorageService.almacenarImagen(archivoTexto));
    }

    @Test
    void testAlmacenarImagenExcedeTamanoLanzaExcepcion() {
        byte[] bytesGrandes = new byte[3 * 1024 * 1024 + 1];
        MockMultipartFile archivoGrande = new MockMultipartFile(
                "archivoImagen",
                "grande.jpg",
                "image/jpeg",
                bytesGrandes
        );

        assertThrows(IllegalArgumentException.class, () -> fileStorageService.almacenarImagen(archivoGrande));
    }

    @Test
    void testEliminarArchivoExistente() throws Exception {
        Path archivo = tempDir.resolve("test-image.jpg");
        Files.writeString(archivo, "img");

        fileStorageService.eliminarArchivo("test-image.jpg");

        assertFalse(Files.exists(archivo));
    }

    @Test
    void testEliminarArchivoConPathTraversalNoSaleDelDirectorio() throws Exception {
        Path archivoFuera = tempDir.getParent().resolve("fuera.txt");
        Files.writeString(archivoFuera, "protegido");

        fileStorageService.eliminarArchivo("../fuera.txt");

        assertTrue(Files.exists(archivoFuera));
        Files.deleteIfExists(archivoFuera);
    }

    @Test
    void testEsImagenValida() {
        assertTrue(fileStorageService.esImagenValida(new MockMultipartFile("a", "a.jpg", "image/jpeg", new byte[]{1})));
        assertTrue(fileStorageService.esImagenValida(new MockMultipartFile("a", "a.png", "image/png", new byte[]{1})));
        assertTrue(fileStorageService.esImagenValida(new MockMultipartFile("a", "a.webp", "image/webp", new byte[]{1})));
        assertFalse(fileStorageService.esImagenValida(new MockMultipartFile("a", "a.gif", "image/gif", new byte[]{1})));
        assertFalse(fileStorageService.esImagenValida(null));
    }
}
