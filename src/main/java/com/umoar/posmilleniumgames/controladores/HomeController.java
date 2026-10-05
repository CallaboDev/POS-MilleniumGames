package com.umoar.posmilleniumgames.controladores;

import com.umoar.posmilleniumgames.servicios.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class HomeController {
    private final ReporteService reporteService;

    public HomeController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/")
    public String index() {
        // Renderiza templates/index.html
        return "index";
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
        return "dashboard";
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

    return "reportes";
}
}