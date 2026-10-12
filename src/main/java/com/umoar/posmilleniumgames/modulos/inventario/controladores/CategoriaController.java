package com.umoar.posmilleniumgames.modulos.inventario.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Categoria;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping("/guardar")
    public String guardarCategoria(@RequestParam("nombre") String nombre,
                                   @RequestParam(value = "redirectUrl", required = false, defaultValue = "/productos") String redirectUrl,
                                   RedirectAttributes redirectAttributes) {
        try {
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoriaService.guardar(categoria);
            redirectAttributes.addFlashAttribute("exito", "Categoría '" + categoria.getNombre() + "' registrada con éxito.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar la categoría: " + e.getMessage());
        }

        return "redirect:" + (redirectUrl.startsWith("/") ? redirectUrl : "/productos");
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarCategoriaApi(@RequestBody Map<String, String> payload) {
        Map<String, Object> respuesta = new HashMap<>();
        try {
            String nombre = payload.get("nombre");
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            Categoria guardada = categoriaService.guardar(categoria);

            respuesta.put("success", true);
            respuesta.put("id", guardada.getId());
            respuesta.put("nombre", guardada.getNombre());
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            respuesta.put("success", false);
            respuesta.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        } catch (Exception e) {
            respuesta.put("success", false);
            respuesta.put("mensaje", "Error al guardar la categoría.");
            return ResponseEntity.internalServerError().body(respuesta);
        }
    }
}
