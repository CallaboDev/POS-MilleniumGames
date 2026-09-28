package com.umoar.posmilleniumgames.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        // Renderiza templates/index.html
        return "index";
    }
}