package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.repositorios.ProductoRepository;
import com.umoar.posmilleniumgames.modulos.usuarios.repositorios.UsuarioRepository;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Venta;
import com.umoar.posmilleniumgames.modulos.ventas.repositorios.ClienteRepository;
import com.umoar.posmilleniumgames.modulos.ventas.repositorios.VentaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteServiceImpl(VentaRepository ventaRepository,
                              ProductoRepository productoRepository,
                              ClienteRepository clienteRepository,
                              UsuarioRepository usuarioRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
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
        BigDecimal ingresos = ventaRepository.sumarIngresosEntre(desde.atStartOfDay(), hastaExclusivo.atStartOfDay());
        return ingresos != null ? ingresos : BigDecimal.ZERO;
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
    public Page<Producto> productosConStockCriticoPaginado(Pageable pageable) {
        return productoRepository.findByStockLessThanEqual(LIMITE_STOCK_CRITICO, pageable);
    }

    @Override
    public List<Object[]> productosMasVendidos() {
        return ventaRepository.buscarProductosMasVendidos().stream().limit(5).toList();
    }

    @Override
    public Page<Object[]> productosMasVendidosPaginado(Pageable pageable) {
        return ventaRepository.buscarProductosMasVendidosPaginado(pageable);
    }

    @Override
    public long totalVideojuegos() {
        return productoRepository.count();
    }

    @Override
    public long totalClientes() {
        return clienteRepository.count();
    }

    @Override
    public long totalPersonal() {
        return usuarioRepository.count();
    }
}
