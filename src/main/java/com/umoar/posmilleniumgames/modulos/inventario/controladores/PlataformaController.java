package com.umoar.posmilleniumgames.modulos.inventario.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Plataforma;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.PlataformaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/plataformas")
public class PlataformaController {

    private final PlataformaService plataformaService;

    public PlataformaController(PlataformaService plataformaService) {
        this.plataformaService = plataformaService;
    }

    @PostMapping("/guardar")
    public String guardarPlataforma(@RequestParam("nombre") String nombre,
                                    @RequestParam(value = "redirectUrl", required = false, defaultValue = "/productos") String redirectUrl,
                                    RedirectAttributes redirectAttributes) {
        try {
            Plataforma plataforma = new Plataforma();
            plataforma.setNombre(nombre);
            plataformaService.guardar(plataforma);
            redirectAttributes.addFlashAttribute("exito", "Plataforma '" + plataforma.getNombre() + "' registrada con éxito.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar la plataforma: " + e.getMessage());
        }

        return "redirect:" + (redirectUrl.startsWith("/") ? redirectUrl : "/productos");
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarPlataformaApi(@RequestBody Map<String, String> payload) {
        Map<String, Object> respuesta = new HashMap<>();
        try {
            String nombre = payload.get("nombre");
            Plataforma plataforma = new Plataforma();
            plataforma.setNombre(nombre);
            Plataforma guardada = plataformaService.guardar(plataforma);

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
            respuesta.put("mensaje", "Error al guardar la plataforma.");
            return ResponseEntity.internalServerError().body(respuesta);
        }
    }
}
