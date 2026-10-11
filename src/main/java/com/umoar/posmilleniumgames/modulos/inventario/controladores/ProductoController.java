package com.umoar.posmilleniumgames.modulos.inventario.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.CategoriaService;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.PlataformaService;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private static final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB
    private static final String UPLOADS_DIR = "uploads";

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final PlataformaService plataformaService;

    public ProductoController(ProductoService productoService,
                              CategoriaService categoriaService,
                              PlataformaService plataformaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.plataformaService = plataformaService;
    }

    @GetMapping
    public String listarProductos(@RequestParam(name = "buscar", required = false) String buscar, Model model) {
        if (buscar != null && !buscar.trim().isEmpty()) {
            model.addAttribute("productos", productoService.buscarPorNombre(buscar));
            model.addAttribute("buscar", buscar);
        } else {
            model.addAttribute("productos", productoService.obtenerTodos());
        }
        return "inventario/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoProductoFormulario(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        model.addAttribute("plataformas", plataformaService.obtenerTodas());
        return "inventario/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarProductoFormulario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaService.obtenerTodas());
            model.addAttribute("plataformas", plataformaService.obtenerTodas());
            return "inventario/formulario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos";
        }
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto,
                                  @RequestParam(value = "archivoImagen", required = false) MultipartFile archivoImagen,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        try {
            // Manejo de la subida de imagen local
            if (archivoImagen != null && !archivoImagen.isEmpty()) {
                // Validar tipo de archivo
                if (!isValidImageFile(archivoImagen)) {
                    throw new IllegalArgumentException("Solo se permiten archivos de imagen (JPG, PNG, WEBP).");
                }

                // Validar tamaño de archivo
                if (archivoImagen.getSize() > MAX_FILE_SIZE) {
                    throw new IllegalArgumentException("El archivo no debe exceder 3MB.");
                }

                String nombreOriginal = archivoImagen.getOriginalFilename();
                String extension = "";
                if (nombreOriginal != null && nombreOriginal.contains(".")) {
                    extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
                }
                String nombreArchivo = UUID.randomUUID() + extension;

                Path directorioUploads = Paths.get(UPLOADS_DIR);
                if (!Files.exists(directorioUploads)) {
                    Files.createDirectories(directorioUploads);
                }

                try (InputStream inputStream = archivoImagen.getInputStream()) {
                    Path rutaArchivo = directorioUploads.resolve(nombreArchivo);
                    Files.copy(inputStream, rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
                }
                producto.setImagen(nombreArchivo);
            } else if (producto.getId() != null) {
                // Conservar imagen existente si estamos editando y no se subió un nuevo archivo
                Producto productoExistente = productoService.obtenerPorId(producto.getId());
                producto.setImagen(productoExistente.getImagen());
            }

            productoService.guardar(producto);
            redirectAttributes.addFlashAttribute("exito", "Producto guardado correctamente.");
            return "redirect:/productos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaService.obtenerTodas());
            model.addAttribute("plataformas", plataformaService.obtenerTodas());
            return "inventario/formulario";
        } catch (IOException e) {
            model.addAttribute("error", "Error al guardar el archivo de la imagen.");
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaService.obtenerTodas());
            model.addAttribute("plataformas", plataformaService.obtenerTodas());
            return "inventario/formulario";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminar(id);
            redirectAttributes.addFlashAttribute("exito", "Producto eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el producto.");
        }
        return "redirect:/productos";
    }

    /**
     * Valida que el archivo sea una imagen con tipo MIME válido.
     */
    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("image/jpeg") ||
                contentType.startsWith("image/png") ||
                contentType.startsWith("image/webp");
    }
}
