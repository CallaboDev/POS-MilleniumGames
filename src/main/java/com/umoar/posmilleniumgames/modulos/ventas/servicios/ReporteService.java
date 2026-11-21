package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    BigDecimal ingresosDelDia(LocalDate fecha);

    long totalVentas();

    BigDecimal ingresosDelMes(LocalDate fecha);

    long ventasDelMes(LocalDate fecha);

    long totalProductosConStockCritico();

    BigDecimal ingresosEntre(LocalDate desde, LocalDate hastaExclusivo);

    long ventasEntre(LocalDate desde, LocalDate hastaExclusivo);

    List<Venta> buscarVentas(LocalDate desde, LocalDate hastaInclusivo);

    List<Producto> productosConStockCritico();

    Page<Producto> productosConStockCriticoPaginado(Pageable pageable);

    List<Object[]> productosMasVendidos();

    Page<Object[]> productosMasVendidosPaginado(Pageable pageable);

    long totalVideojuegos();

    long totalClientes();

    long totalPersonal();
}
