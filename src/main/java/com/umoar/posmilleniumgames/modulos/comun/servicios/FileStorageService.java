package com.umoar.posmilleniumgames.modulos.comun.servicios;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Valida, procesa y guarda un archivo de imagen en el directorio de almacenamiento.
     *
     * @param archivo el archivo multipart recibido en la petición.
     * @return el nombre único del archivo guardado.
     */
    String almacenarImagen(MultipartFile archivo);

    /**
     * Elimina un archivo físico del almacenamiento si existe.
     *
     * @param nombreArchivo el nombre del archivo a eliminar.
     */
    void eliminarArchivo(String nombreArchivo);

    /**
     * Comprueba si el archivo proporcionado tiene formato de imagen permitido.
     *
     * @param archivo el archivo a evaluar.
     * @return true si es una imagen válida (JPEG, PNG, WEBP).
     */
    boolean esImagenValida(MultipartFile archivo);
}
