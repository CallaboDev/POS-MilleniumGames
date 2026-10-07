package com.umoar.posmilleniumgames.configuracion;

import com.umoar.posmilleniumgames.modelos.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Locale;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String USUARIO_LOGUEADO = "usuarioLogueado";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {
        HttpSession session = request.getSession(false);
        Object usuarioEnSesion = session == null ? null : session.getAttribute(USUARIO_LOGUEADO);

        if (!(usuarioEnSesion instanceof Usuario usuario)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        if (esRutaAdministrativa(requestPath) && !esAdministrador(usuario)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }


        return true;
    }

    private boolean esRutaAdministrativa(String path) {
        return perteneceARuta(path, "/usuarios") || perteneceARuta(path, "/reportes");
    }

    private boolean perteneceARuta(String path, String base) {
        return path.equals(base) || path.startsWith(base + "/");
    }


    private boolean esAdministrador(Usuario usuario) {
        return usuario.getRol() != null
                && "ADMIN".equals(usuario.getRol().trim().toUpperCase(Locale.ROOT));
    }

}
