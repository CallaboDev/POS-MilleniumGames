package com.umoar.posmilleniumgames.modulos.ventas.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.repositorios.ProductoRepository;
import com.umoar.posmilleniumgames.modulos.usuarios.repositorios.UsuarioRepository;
import com.umoar.posmilleniumgames.modulos.ventas.repositorios.ClienteRepository;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class HomeController {
    private final ReporteService reporteService;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public HomeController(ReporteService reporteService,
                          ProductoRepository productoRepository,
                          ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository) {
        this.reporteService = reporteService;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        LocalDate hoy = LocalDate.now();
        model.addAttribute("totalVideojuegos", productoRepository.count());
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("ventasHoy", reporteService.ingresosDelDia(hoy));
        model.addAttribute("totalPersonal", usuarioRepository.count());
        return "dashboard/index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        LocalDate hoy = LocalDate.now();
        model.addAttribute("ingresosHoy", reporteService.ingresosDelDia(hoy));
        model.addAttribute("ingresosMes", reporteService.ingresosDelMes(hoy));
        model.addAttribute("ventasMes", reporteService.ventasDelMes(hoy));
        model.addAttribute("totalVentas", reporteService.totalVentas());
        model.addAttribute("productosPorAgotarse", reporteService.totalProductosConStockCritico());
        model.addAttribute("stockCritico", reporteService.productosConStockCritico());
        return "dashboard/dashboard";
    }

    @GetMapping("/reportes")
    public String reportes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        LocalDate hoy = LocalDate.now();

        // Default del rango al mes actual
        desde = (desde == null) ? hoy.withDayOfMonth(1) : desde;
        hasta = (hasta == null) ? hoy : hasta;

        // Validación
        if (desde.isAfter(hasta)) {
            model.addAttribute("errorFechas", "La fecha Desde no puede ser posterior a la fecha Hasta.");
            desde = hasta;
        }

        // Mantener la misma convención en todo el reporte:
        // [desde, hasta] en UI y consulta semiabierta [desde, hasta + 1 día) en SQL.
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("ventas", reporteService.buscarVentas(desde, hasta));
        model.addAttribute("ingresosPeriodo", reporteService.ingresosEntre(desde, hasta.plusDays(1)));
        model.addAttribute("cantidadVentas", reporteService.ventasEntre(desde, hasta.plusDays(1)));
        model.addAttribute("stockCritico", reporteService.productosConStockCritico());
        model.addAttribute("productosMasVendidos", reporteService.productosMasVendidos());

        return "ventas/reportes";
    }
}
