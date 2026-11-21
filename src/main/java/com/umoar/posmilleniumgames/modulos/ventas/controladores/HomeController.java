package com.umoar.posmilleniumgames.modulos.ventas.controladores;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.ventas.servicios.ReporteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    public static final int REPORTES_PAGE_SIZE = 15;

    private final ReporteService reporteService;

    public HomeController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/")
    public String index(Model model) {
        LocalDate hoy = LocalDate.now();
        model.addAttribute("totalVideojuegos", reporteService.totalVideojuegos());
        model.addAttribute("totalClientes", reporteService.totalClientes());
        model.addAttribute("ventasHoy", reporteService.ingresosDelDia(hoy));
        model.addAttribute("totalPersonal", reporteService.totalPersonal());
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
            @RequestParam(defaultValue = "0") int pageMasVendidos,
            @RequestParam(defaultValue = "0") int pageStockCritico,
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

        Page<Object[]> paginaMasVendidos = obtenerPaginaMasVendidos(pageMasVendidos);
        Page<Producto> paginaStockCritico = obtenerPaginaStockCritico(pageStockCritico);

        // Mantener la misma convención en todo el reporte:
        // [desde, hasta] en UI y consulta semiabierta [desde, hasta + 1 día) en SQL.
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("ventas", reporteService.buscarVentas(desde, hasta));
        model.addAttribute("ingresosPeriodo", reporteService.ingresosEntre(desde, hasta.plusDays(1)));
        model.addAttribute("cantidadVentas", reporteService.ventasEntre(desde, hasta.plusDays(1)));
        model.addAttribute("stockCriticoPage", paginaStockCritico);
        model.addAttribute("stockCritico", paginaStockCritico.getContent());
        model.addAttribute("productosMasVendidosPage", paginaMasVendidos);
        model.addAttribute("productosMasVendidos", paginaMasVendidos.getContent());

        return "ventas/reportes";
    }

    @GetMapping("/reportes/api/mas-vendidos")
    @ResponseBody
    public PaginaReporteDTO<ProductoMasVendidoItemDTO> apiMasVendidos(
            @RequestParam(defaultValue = "0") int page) {
        Page<Object[]> pagina = obtenerPaginaMasVendidos(page);
        List<ProductoMasVendidoItemDTO> items = pagina.getContent().stream()
                .map(fila -> new ProductoMasVendidoItemDTO(
                        fila[0] != null ? fila[0].toString() : "",
                        fila[1] instanceof Number num ? num.longValue() : 0L,
                        fila[2] instanceof BigDecimal bd ? bd : BigDecimal.ZERO
                ))
                .toList();
        return new PaginaReporteDTO<>(
                items,
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages()
        );
    }

    @GetMapping("/reportes/api/stock-critico")
    @ResponseBody
    public PaginaReporteDTO<StockCriticoItemDTO> apiStockCritico(
            @RequestParam(defaultValue = "0") int page) {
        Page<Producto> pagina = obtenerPaginaStockCritico(page);
        List<StockCriticoItemDTO> items = pagina.getContent().stream()
                .map(prod -> new StockCriticoItemDTO(
                        prod.getNombre(),
                        prod.getSku(),
                        prod.getStock() != null ? prod.getStock() : 0
                ))
                .toList();
        return new PaginaReporteDTO<>(
                items,
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages()
        );
    }

    private Page<Object[]> obtenerPaginaMasVendidos(int page) {
        int paginaSegura = Math.max(0, page);
        Pageable pageable = PageRequest.of(paginaSegura, REPORTES_PAGE_SIZE);
        Page<Object[]> pagina = reporteService.productosMasVendidosPaginado(pageable);
        if (paginaSegura > 0 && pagina.getTotalPages() > 0 && paginaSegura >= pagina.getTotalPages()) {
            pagina = reporteService.productosMasVendidosPaginado(
                    PageRequest.of(pagina.getTotalPages() - 1, REPORTES_PAGE_SIZE)
            );
        }
        return pagina;
    }

    private Page<Producto> obtenerPaginaStockCritico(int page) {
        int paginaSegura = Math.max(0, page);
        Sort orden = Sort.by(Sort.Order.asc("stock"), Sort.Order.asc("id"));
        Pageable pageable = PageRequest.of(paginaSegura, REPORTES_PAGE_SIZE, orden);
        Page<Producto> pagina = reporteService.productosConStockCriticoPaginado(pageable);
        if (paginaSegura > 0 && pagina.getTotalPages() > 0 && paginaSegura >= pagina.getTotalPages()) {
            pagina = reporteService.productosConStockCriticoPaginado(
                    PageRequest.of(pagina.getTotalPages() - 1, REPORTES_PAGE_SIZE, orden)
            );
        }
        return pagina;
    }

    public record ProductoMasVendidoItemDTO(String nombre, long unidades, BigDecimal ingresos) {
    }

    public record StockCriticoItemDTO(String nombre, String sku, int stock) {
    }

    public record PaginaReporteDTO<T>(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
    }
}
