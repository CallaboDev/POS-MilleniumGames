package com.umoar.posmilleniumgames.configuracion;

import com.umoar.posmilleniumgames.modelos.Rol;
import com.umoar.posmilleniumgames.modelos.Usuario;
import com.umoar.posmilleniumgames.repositorios.RolRepository;
import com.umoar.posmilleniumgames.repositorios.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        crearRolSiNoExiste("ROLE_ADMIN");
        crearRolSiNoExiste("ROLE_CAJERO");

        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setNombre("Administrador del Sistema");
            admin.setEnabled(true);

            Optional<Rol> adminRol = rolRepository.findByNombre("ROLE_ADMIN");
            adminRol.ifPresent(rol -> admin.getRoles().add(rol));

            usuarioRepository.save(admin);
            System.out.println("Seeder: Usuario 'admin' creado con xito.");
        }
    }

    private void crearRolSiNoExiste(String nombre) {
        if (rolRepository.findByNombre(nombre).isEmpty()) {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rolRepository.save(rol);
            System.out.println("Seeder: Rol '" + nombre + "' creado con xito.");
        }
    }
}
