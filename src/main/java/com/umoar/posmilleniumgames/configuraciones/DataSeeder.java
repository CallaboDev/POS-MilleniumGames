package com.umoar.posmilleniumgames.configuraciones;

import com.umoar.posmilleniumgames.modulos.inventario.servicios.CategoriaService;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.PlataformaService;
import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Rol;
import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Usuario;
import com.umoar.posmilleniumgames.modulos.usuarios.repositorios.RolRepository;
import com.umoar.posmilleniumgames.modulos.usuarios.repositorios.UsuarioRepository;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ClienteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoriaService categoriaService;
    private final PlataformaService plataformaService;
    private final ClienteService clienteService;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      RolRepository rolRepository,
                      PasswordEncoder passwordEncoder,
                      CategoriaService categoriaService,
                      PlataformaService plataformaService,
                      ClienteService clienteService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoriaService = categoriaService;
        this.plataformaService = plataformaService;
        this.clienteService = clienteService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Roles de usuario
        crearRolSiNoExiste("ROLE_ADMIN");
        crearRolSiNoExiste("ROLE_CAJERO");

        // Usuario administrador predeterminado
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setNombre("Administrador del Sistema");
            admin.setEnabled(true);

            Optional<Rol> adminRol = rolRepository.findByNombre("ROLE_ADMIN");
            adminRol.ifPresent(rol -> admin.getRoles().add(rol));

            usuarioRepository.save(admin);
            System.out.println("Seeder: Usuario 'admin' creado con éxito.");
        }

        // Categorías iniciales de catálogo
        categoriaService.crearSiNoExiste("Videojuegos");
        categoriaService.crearSiNoExiste("Consolas");
        categoriaService.crearSiNoExiste("Accesorios");
        categoriaService.crearSiNoExiste("Coleccionables");
        categoriaService.crearSiNoExiste("Servicios");

        // Plataformas iniciales de catálogo
        plataformaService.crearSiNoExiste("PlayStation 5");
        plataformaService.crearSiNoExiste("PlayStation 4");
        plataformaService.crearSiNoExiste("Nintendo Switch");
        plataformaService.crearSiNoExiste("Xbox Series X/S");
        plataformaService.crearSiNoExiste("Xbox One");
        plataformaService.crearSiNoExiste("PC Gaming");
        plataformaService.crearSiNoExiste("Retro / Clásicos");

        // Cliente Consumidor Final predeterminado
        clienteService.obtenerOCrearConsumidorFinal();
    }

    private void crearRolSiNoExiste(String nombre) {
        if (rolRepository.findByNombre(nombre).isEmpty()) {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rolRepository.save(rol);
            System.out.println("Seeder: Rol '" + nombre + "' creado con éxito.");
        }
    }
}
