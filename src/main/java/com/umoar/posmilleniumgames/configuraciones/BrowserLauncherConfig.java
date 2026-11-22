package com.umoar.posmilleniumgames.configuraciones;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@Profile("!test")
@ConditionalOnProperty(name = "app.browser.auto-open", havingValue = "true", matchIfMissing = true)
public class BrowserLauncherConfig {

    static final String BROWSER_OPENED_SYSTEM_PROPERTY = "pos.milleniumgames.browser.opened";

    @EventListener(ApplicationReadyEvent.class)
    public void abrirNavegadorAlIniciar(ApplicationReadyEvent event) {
        // 1. Solo abrir si hay un servidor web embebido real escuchando (evita @SpringBootTest en modo MOCK)
        if (event == null
                || !(event.getApplicationContext() instanceof WebServerApplicationContext webServerContext)
                || webServerContext.getWebServer() == null) {
            return;
        }

        // 2. Evitar apertura durante la ejecución de pruebas automatizadas (incluso con RANDOM_PORT / DEFINED_PORT)
        if (esEjecucionDePruebas()) {
            return;
        }

        // 3. Evitar la ejecución en entornos de producción/cloud como Render o servidores sin entorno gráfico
        if (esServidorORender()) {
            System.out.println("Servidor o entorno Render detectado. Se omite la apertura automática del navegador web.");
            return;
        }

        // 4. Evitar reabrir pestañas en reinicios en caliente de spring-boot-devtools (RestartClassLoader)
        if (System.getProperties().putIfAbsent(BROWSER_OPENED_SYSTEM_PROPERTY, "true") != null) {
            return;
        }

        String appUrl = construirUrlAplicacion(webServerContext);
        abrirNavegador(appUrl);
    }

    protected void abrirNavegador(String appUrl) {
        String os = System.getProperty("os.name", "").toLowerCase();

        try {
            if (os.contains("win") || os.contains("mac")) {
                abrirEnWindowsOMac(os, appUrl);
            } else if (os.contains("linux") || os.contains("nix") || os.contains("nux")) {
                abrirEnLinux(appUrl);
            } else {
                System.err.println("Advertencia: Sistema operativo no reconocido (" + os + "). Por favor abre manualmente: " + appUrl);
            }
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo abrir el navegador automáticamente: " + e.getMessage());
        }
    }

    private String construirUrlAplicacion(WebServerApplicationContext webServerContext) {
        int port = webServerContext.getWebServer().getPort();
        String contextPath = webServerContext.getEnvironment().getProperty("server.servlet.context-path", "");
        if (!contextPath.isBlank() && !contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }
        return "http://localhost:" + port + contextPath;
    }

    private void abrirEnWindowsOMac(String os, String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                if (os.contains("win")) {
                    new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
                } else if (os.contains("mac")) {
                    new ProcessBuilder("open", url).start();
                }
            }
        } catch (IOException | URISyntaxException e) {
            System.err.println("Advertencia: Error al abrir el navegador en " + os + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo abrir el navegador con Desktop: " + e.getMessage());
        }
    }

    private void abrirEnLinux(String url) {
        String[] comandos = {"xdg-open", "brave", "google-chrome", "firefox"};
        boolean abierto = false;

        for (String comando : comandos) {
            try {
                new ProcessBuilder(comando, url).start();
                abierto = true;
                break;
            } catch (IOException e) {
                // Si el comando no está disponible en el sistema, probar con el siguiente
            }
        }

        if (!abierto) {
            System.err.println("Advertencia: No se encontró ningún comando disponible (xdg-open, brave, google-chrome) para abrir el navegador en Linux. Acceda a: " + url);
        }
    }

    protected boolean esEjecucionDePruebas() {
        return StackWalker.getInstance().walk(frames -> frames.anyMatch(frame -> {
            String className = frame.getClassName();
            return className.startsWith("org.junit.")
                    || className.startsWith("org.springframework.test.")
                    || className.startsWith("org.apache.maven.surefire.")
                    || className.startsWith("org.apache.maven.failsafe.")
                    || className.startsWith("com.intellij.rt.junit.")
                    || className.startsWith("worker.org.gradle.");
        }));
    }

    protected boolean esServidorORender() {
        // Render inyecta la variable de entorno RENDER=true
        String renderEnv = System.getenv("RENDER");
        if (renderEnv != null && !renderEnv.isBlank()) {
            return true;
        }

        // Si es un entorno Linux sin interfaz gráfica (sin servidor X11 ni Wayland)
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("linux") || os.contains("nix") || os.contains("nux")) {
            String display = System.getenv("DISPLAY");
            String waylandDisplay = System.getenv("WAYLAND_DISPLAY");
            return display == null && waylandDisplay == null;
        }

        return false;
    }
}
