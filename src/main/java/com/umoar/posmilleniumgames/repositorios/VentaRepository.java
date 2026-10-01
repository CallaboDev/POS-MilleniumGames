package com.umoar.posmilleniumgames.repositorios;

import com.umoar.posmilleniumgames.modelos.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("select coalesce(sum(v.total), 0) from Venta v where v.fechaHora >= :inicio and v.fechaHora < :fin")
    BigDecimal sumarIngresosEntre(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("select count(v) from Venta v where v.fechaHora >= :inicio and v.fechaHora < :fin")
    long contarVentasEntre(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("select distinct v from Venta v left join fetch v.cliente left join fetch v.detalles d left join fetch d.producto where v.fechaHora >= :inicio and v.fechaHora < :fin order by v.fechaHora desc")
    List<Venta> buscarVentasEntre(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("select d.producto.nombre, sum(d.cantidad), sum(d.subtotal) from DetalleVenta d group by d.producto.id, d.producto.nombre order by sum(d.cantidad) desc")
    List<Object[]> buscarProductosMasVendidos();
}
