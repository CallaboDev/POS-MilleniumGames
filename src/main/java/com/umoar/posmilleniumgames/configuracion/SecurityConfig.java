package com.umoar.posmilleniumgames.configuracion;

import com.umoar.posmilleniumgames.servicios.UsuarioDetallesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioDetallesService usuarioDetallesService;

    public SecurityConfig(UsuarioDetallesService usuarioDetallesService) {
        this.usuarioDetallesService = usuarioDetallesService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**", "/webjars/**").permitAll()
                .requestMatchers("/login", "/logout").permitAll()
                .requestMatchers("/reportes/**", "/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/", "/dashboard", "/caja/**", "/productos/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
