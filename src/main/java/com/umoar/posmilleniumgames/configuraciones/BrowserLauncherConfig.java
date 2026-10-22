package com.umoar.posmilleniumgames.configuraciones;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class BrowserLauncherConfig {

    private static final String APP_URL = "http://localhost:8080";

    @EventListener(ApplicationReadyEvent.class)
    public void abrirNavegadorAlIniciar() {
        // 1. Evitar la ejecución en entornos de producción/cloud como Render o servidores sin entorno gráfico
        if (esServidorORender()) {
            System.out.println("Servidor o entorno Render detectado. Se omite la apertura automática del navegador web.");
            return;
        }

        // 2. Detección multiplataforma del sistema operativo
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win") || os.contains("mac")) {
                // 3. Para Windows y macOS, intentar usar java.awt.Desktop.getDesktop().browse()
                abrirEnWindowsOMac(os, APP_URL);
            } else if (os.contains("linux") || os.contains("nix") || os.contains("nux")) {
                // 4. Para Linux, usar Runtime.getRuntime().exec() con xdg-open (o brave / google-chrome como fallback)
                abrirEnLinux(APP_URL);
            } else {
                System.err.println("Advertencia: Sistema operativo no reconocido (" + os + "). Por favor abre manualmente: " + APP_URL);
            }
        } catch (Exception e) {
            // 5. Manejo general de excepciones para asegurar que Spring Boot no se detenga
            System.err.println("Advertencia: No se pudo abrir el navegador automáticamente: " + e.getMessage());
        }
    }

    private void abrirEnWindowsOMac(String os, String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Fallback por comandos si Desktop no está disponible o está en modo headless
                if (os.contains("win")) {
                    Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
                } else if (os.contains("mac")) {
                    Runtime.getRuntime().exec(new String[]{"open", url});
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
                Runtime.getRuntime().exec(new String[]{comando, url});
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

    private boolean esServidorORender() {
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
