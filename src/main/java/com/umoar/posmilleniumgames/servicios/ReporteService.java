package com.umoar.posmilleniumgames.servicios;

import com.umoar.posmilleniumgames.modelos.Producto;
import com.umoar.posmilleniumgames.modelos.Venta;

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

    List<Object[]> productosMasVendidos();
}
