package com.umoar.posmilleniumgames.configuracion;

import com.umoar.posmilleniumgames.modelos.Usuario;
import com.umoar.posmilleniumgames.servicios.UsuarioService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Configuration
public class SeguridadConfig {

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$.{53}$");

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider usuarioAuthenticationProvider(UsuarioService usuarioService,
                                                                PasswordEncoder passwordEncoder) {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String submittedPassword = String.valueOf(authentication.getCredentials());
                Usuario usuario = usuarioService.buscarPorUsername(username)
                        .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));
                String storedPassword = usuario.getPassword();
                if (storedPassword == null || !coincide(submittedPassword, storedPassword, passwordEncoder)) {
                    throw new BadCredentialsException("Credenciales incorrectas");
                }

                if (!BCRYPT_PATTERN.matcher(storedPassword).matches()) {
                    usuario.setPassword(passwordEncoder.encode(submittedPassword));
                    usuarioService.actualizar(usuario);
                    storedPassword = usuario.getPassword();
                }

                String role = usuario.getRol() == null
                        ? ""
                        : usuario.getRol().trim().toUpperCase(Locale.ROOT);
                var principal = User.withUsername(usuario.getUsername())
                        .password(storedPassword)
                        .authorities(role.isEmpty() ? List.of() : List.of(new SimpleGrantedAuthority("ROLE_" + role)))
                        .build();
                return UsernamePasswordAuthenticationToken.authenticated(
                        principal, null, principal.getAuthorities());
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationProvider usuarioAuthenticationProvider,
                                                   UsuarioService usuarioService) throws Exception {
        http
                .authenticationProvider(usuarioAuthenticationProvider)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/autenticar", "/css/**", "/js/**", "/img/**", "/uploads/**").permitAll()
                        .requestMatchers(
                                "/usuarios", "/usuarios/**",
                                "/reportes", "/reportes/**",
                                "/dashboard", "/dashboard/**",
                                "/inventario", "/inventario/**",
                                "/configuracion", "/configuracion/**",
                                "/roles", "/roles/**",
                                "/empleados", "/empleados/**"
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/autenticar")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            Usuario usuario = usuarioService.buscarPorUsername(authentication.getName())
                                    .orElseThrow(() -> new BadCredentialsException("Usuario autenticado no encontrado"));
                            request.getSession(true).setAttribute("usuarioLogueado", usuario);
                            response.sendRedirect(request.getContextPath() + "/");
                        })
                        .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error")))
                .logout(logout -> logout.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect(request.getContextPath() + "/login"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect(request.getContextPath() + "/login?denied")));

        return http.build();
    }

    private static boolean coincide(String submittedPassword,
                                    String storedPassword,
                                    PasswordEncoder passwordEncoder) {
        if (BCRYPT_PATTERN.matcher(storedPassword).matches()) {
            return passwordEncoder.matches(submittedPassword, storedPassword);
        }
        return MessageDigest.isEqual(
                submittedPassword.getBytes(StandardCharsets.UTF_8),
                storedPassword.getBytes(StandardCharsets.UTF_8));
    }
}
