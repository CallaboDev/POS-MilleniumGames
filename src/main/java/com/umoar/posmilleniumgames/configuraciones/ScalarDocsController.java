package com.umoar.posmilleniumgames.configuraciones;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ScalarDocsController {

    @GetMapping(value = {"/docs", "/docs/"}, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String docs() {
        return """
            <!doctype html>
            <html lang="es">
              <head>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1" />
                <title>POS Millenium Games - Documentación de API (Scalar)</title>
                <link rel="icon" type="image/svg+xml" href="https://scalar.com/favicon.svg" />
                <style>
                  body {
                    margin: 0;
                    padding: 0;
                    height: 100vh;
                  }
                </style>
              </head>
              <body>
                <script
                  id="api-reference"
                  data-url="/v3/api-docs"
                  src="https://cdn.jsdelivr.net/npm/@scalar/api-reference"></script>
              </body>
            </html>
            """;
    }
}
