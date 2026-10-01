package com.umoar.posmilleniumgames.servicios;

import com.umoar.posmilleniumgames.modelos.Producto;
import com.umoar.posmilleniumgames.modelos.Venta;
import com.umoar.posmilleniumgames.repositorios.ProductoRepository;
import com.umoar.posmilleniumgames.repositorios.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {
    private static final int LIMITE_STOCK_CRITICO = 3;

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public ReporteServiceImpl(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public BigDecimal ingresosDelDia(LocalDate fecha) {
        return ingresosEntre(fecha, fecha.plusDays(1));
    }

    @Override
    public long totalVentas() {
        return ventaRepository.count();
    }

    @Override
    public BigDecimal ingresosDelMes(LocalDate fecha) {
        LocalDate inicioMes = fecha.withDayOfMonth(1);
        return ingresosEntre(inicioMes, inicioMes.plusMonths(1));
    }

    @Override
    public long ventasDelMes(LocalDate fecha) {
        LocalDate inicioMes = fecha.withDayOfMonth(1);
        return ventasEntre(inicioMes, inicioMes.plusMonths(1));
    }

    @Override
    public long totalProductosConStockCritico() {
        return productoRepository.countByStockLessThanEqual(LIMITE_STOCK_CRITICO);
    }

    @Override
    public BigDecimal ingresosEntre(LocalDate desde, LocalDate hastaExclusivo) {
        return ventaRepository.sumarIngresosEntre(desde.atStartOfDay(), hastaExclusivo.atStartOfDay());
    }

    @Override
    public long ventasEntre(LocalDate desde, LocalDate hastaExclusivo) {
        return ventaRepository.contarVentasEntre(desde.atStartOfDay(), hastaExclusivo.atStartOfDay());
    }

    @Override
    public List<Venta> buscarVentas(LocalDate desde, LocalDate hastaInclusivo) {
        return ventaRepository.buscarVentasEntre(desde.atStartOfDay(), hastaInclusivo.plusDays(1).atStartOfDay());
    }

    @Override
    public List<Producto> productosConStockCritico() {
        return productoRepository.findByStockLessThanEqualOrderByStockAsc(LIMITE_STOCK_CRITICO);
    }

    @Override
    public List<Object[]> productosMasVendidos() {
        return ventaRepository.buscarProductosMasVendidos().stream().limit(5).toList();
    }
}
