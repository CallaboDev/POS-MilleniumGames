package com.umoar.posmilleniumgames.configuraciones;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BrowserLauncherConfigTest {

    @BeforeEach
    void limpiarPropiedad() {
        System.clearProperty(BrowserLauncherConfig.BROWSER_OPENED_SYSTEM_PROPERTY);
    }

    @AfterEach
    void restaurarPropiedad() {
        System.clearProperty(BrowserLauncherConfig.BROWSER_OPENED_SYSTEM_PROPERTY);
    }

    @Test
    void detectaEjecucionDesdeJUnit() {
        BrowserLauncherConfig launcher = new BrowserLauncherConfig();
        assertTrue(launcher.esEjecucionDePruebas());
    }

    @Test
    void noAbreNavegadorEnEjecucionDePruebasInclusoConServidorWeb() {
        List<String> urlsAbiertas = new ArrayList<>();
        BrowserLauncherConfig launcher = new BrowserLauncherConfig() {
            @Override
            protected boolean esServidorORender() {
                return false;
            }

            @Override
            protected void abrirNavegador(String appUrl) {
                urlsAbiertas.add(appUrl);
            }
        };

        ApplicationReadyEvent event = crearEventoConServidorWeb(8080, "");
        launcher.abrirNavegadorAlIniciar(event);

        assertTrue(urlsAbiertas.isEmpty());
        assertNull(System.getProperty(BrowserLauncherConfig.BROWSER_OPENED_SYSTEM_PROPERTY));
    }

    @Test
    void noAbreNavegadorCuandoContextoNoTieneServidorWeb() {
        List<String> urlsAbiertas = new ArrayList<>();
        BrowserLauncherConfig launcher = new BrowserLauncherConfig() {
            @Override
            protected boolean esEjecucionDePruebas() {
                return false;
            }

            @Override
            protected boolean esServidorORender() {
                return false;
            }

            @Override
            protected void abrirNavegador(String appUrl) {
                urlsAbiertas.add(appUrl);
            }
        };

        GenericWebApplicationContext mockContext = new GenericWebApplicationContext();
        ApplicationReadyEvent event = mock(ApplicationReadyEvent.class);
        when(event.getApplicationContext()).thenReturn(mockContext);

        launcher.abrirNavegadorAlIniciar(event);

        assertTrue(urlsAbiertas.isEmpty());
        assertNull(System.getProperty(BrowserLauncherConfig.BROWSER_OPENED_SYSTEM_PROPERTY));
    }

    @Test
    void abreNavegadorUnaSolaVezInclusoTrasReiniciosDeDevTools() {
        List<String> urlsAbiertas = new ArrayList<>();

        BrowserLauncherConfig primerArranque = new BrowserLauncherConfig() {
            @Override
            protected boolean esEjecucionDePruebas() {
                return false;
            }

            @Override
            protected boolean esServidorORender() {
                return false;
            }

            @Override
            protected void abrirNavegador(String appUrl) {
                urlsAbiertas.add(appUrl);
            }
        };

        BrowserLauncherConfig reinicioDevTools = new BrowserLauncherConfig() {
            @Override
            protected boolean esEjecucionDePruebas() {
                return false;
            }

            @Override
            protected boolean esServidorORender() {
                return false;
            }

            @Override
            protected void abrirNavegador(String appUrl) {
                urlsAbiertas.add(appUrl);
            }
        };

        ApplicationReadyEvent eventoInicial = crearEventoConServidorWeb(9090, "/pos");
        ApplicationReadyEvent eventoReinicio = crearEventoConServidorWeb(9090, "/pos");

        primerArranque.abrirNavegadorAlIniciar(eventoInicial);
        reinicioDevTools.abrirNavegadorAlIniciar(eventoReinicio);

        assertEquals(List.of("http://localhost:9090/pos"), urlsAbiertas);
        assertEquals("true", System.getProperty(BrowserLauncherConfig.BROWSER_OPENED_SYSTEM_PROPERTY));
    }

    private ApplicationReadyEvent crearEventoConServidorWeb(int port, String contextPath) {
        WebServer webServer = mock(WebServer.class);
        when(webServer.getPort()).thenReturn(port);

        MockEnvironment environment = new MockEnvironment();
        if (!contextPath.isEmpty()) {
            environment.setProperty("server.servlet.context-path", contextPath);
        }

        ServletWebServerApplicationContext context = mock(ServletWebServerApplicationContext.class);
        when(context.getWebServer()).thenReturn(webServer);
        when(context.getEnvironment()).thenReturn(environment);

        ApplicationReadyEvent event = mock(ApplicationReadyEvent.class);
        when(event.getApplicationContext()).thenReturn(context);
        return event;
    }
}
