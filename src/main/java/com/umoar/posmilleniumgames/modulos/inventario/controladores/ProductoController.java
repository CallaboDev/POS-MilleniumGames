package com.umoar.posmilleniumgames.modulos.inventario.controladores;

import com.umoar.posmilleniumgames.modulos.comun.servicios.FileStorageService;
import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.CategoriaService;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.PlataformaService;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final PlataformaService plataformaService;
    private final FileStorageService fileStorageService;

    public ProductoController(ProductoService productoService,
                              CategoriaService categoriaService,
                              PlataformaService plataformaService,
                              FileStorageService fileStorageService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.plataformaService = plataformaService;
        this.fileStorageService = fileStorageService;
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
        String imagenAnterior = null;
        String nuevoArchivoSubido = null;
        try {
            if (producto.getId() != null) {
                Producto productoExistente = productoService.obtenerPorId(producto.getId());
                imagenAnterior = productoExistente.getImagen();
            }

            if (archivoImagen != null && !archivoImagen.isEmpty()) {
                nuevoArchivoSubido = fileStorageService.almacenarImagen(archivoImagen);
                producto.setImagen(nuevoArchivoSubido);
            } else if (imagenAnterior != null) {
                // Conservar imagen existente si estamos editando y no se subió un nuevo archivo
                producto.setImagen(imagenAnterior);
            }

            productoService.guardar(producto);

            if (nuevoArchivoSubido != null && imagenAnterior != null && !imagenAnterior.equals(nuevoArchivoSubido)) {
                fileStorageService.eliminarArchivo(imagenAnterior);
            }

            redirectAttributes.addFlashAttribute("exito", "Producto guardado correctamente.");
            return "redirect:/productos";
        } catch (IllegalArgumentException e) {
            limpiarArchivoFallido(nuevoArchivoSubido, producto, imagenAnterior);
            cargarDatosFormulario(model, producto, e.getMessage());
            return "inventario/formulario";
        } catch (Exception e) {
            limpiarArchivoFallido(nuevoArchivoSubido, producto, imagenAnterior);
            cargarDatosFormulario(model, producto, "Error al guardar el producto: " + e.getMessage());
            return "inventario/formulario";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            String imagen = producto.getImagen();
            productoService.eliminar(id);
            if (imagen != null) {
                fileStorageService.eliminarArchivo(imagen);
            }
            redirectAttributes.addFlashAttribute("exito", "Producto eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el producto.");
        }
        return "redirect:/productos";
    }

    private void limpiarArchivoFallido(String nuevoArchivoSubido, Producto producto, String imagenAnterior) {
        if (nuevoArchivoSubido != null) {
            fileStorageService.eliminarArchivo(nuevoArchivoSubido);
        }
        producto.setImagen(imagenAnterior);
    }

    private void cargarDatosFormulario(Model model, Producto producto, String mensajeError) {
        model.addAttribute("error", mensajeError);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        model.addAttribute("plataformas", plataformaService.obtenerTodas());
    }
}
