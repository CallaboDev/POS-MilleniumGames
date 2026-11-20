package com.umoar.posmilleniumgames.modulos.comun.servicios;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB
    private static final String UPLOADS_DIR = "uploads";

    private final Path directorioUploads;

    public FileStorageServiceImpl() {
        this(Paths.get(UPLOADS_DIR));
    }

    FileStorageServiceImpl(Path directorioUploads) {
        this.directorioUploads = directorioUploads.toAbsolutePath().normalize();
    }

    @Override
    public String almacenarImagen(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede estar vacío.");
        }

        if (!esImagenValida(archivo)) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen (JPG, PNG, WEBP).");
        }

        if (archivo.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo no debe exceder 3MB.");
        }

        String nombreOriginal = archivo.getOriginalFilename();
        String extension = "";
        if (nombreOriginal != null && !nombreOriginal.isBlank()) {
            String nombreLimpio = Paths.get(nombreOriginal).getFileName().toString();
            int ultimoPunto = nombreLimpio.lastIndexOf('.');
            if (ultimoPunto >= 0) {
                extension = nombreLimpio.substring(ultimoPunto);
            }
        }

        String nombreArchivo = UUID.randomUUID() + extension;

        try {
            if (!Files.exists(directorioUploads)) {
                Files.createDirectories(directorioUploads);
            }

            Path rutaArchivo = directorioUploads.resolve(nombreArchivo).normalize();
            if (!rutaArchivo.startsWith(directorioUploads)) {
                throw new IllegalArgumentException("Nombre de archivo inválido.");
            }

            try (InputStream inputStream = archivo.getInputStream()) {
                Files.copy(inputStream, rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
            }

            return nombreArchivo;
        } catch (IOException e) {
            throw new UncheckedIOException("Error al guardar el archivo de la imagen en disco.", e);
        }
    }

    @Override
    public void eliminarArchivo(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            return;
        }

        String nombreSeguro = Paths.get(nombreArchivo).getFileName().toString();
        Path rutaArchivo = directorioUploads.resolve(nombreSeguro).normalize();
        if (!rutaArchivo.startsWith(directorioUploads)) {
            return;
        }

        try {
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo {}: {}", nombreArchivo, e.getMessage());
        }
    }

    @Override
    public boolean esImagenValida(MultipartFile archivo) {
        if (archivo == null) {
            return false;
        }
        String contentType = archivo.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("image/jpeg") ||
                contentType.startsWith("image/png") ||
                contentType.startsWith("image/webp");
    }
}
