package com.umoar.posmilleniumgames.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
@Getter
@Setter
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código correlativo del ticket o factura emitida (ej: "FAC-0001", "TICKET-2026-00125")
    @Column(unique = true, nullable = false)
    private String numeroFactura;

    // Momento exacto del cobro (ej: 2026-09-26T15:30:00)
    @Column(nullable = false)
    private LocalDateTime fechaHora;

    // Monto final a pagar tras sumar artículos y restar descuentos (ej: 139.98)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // Forma en la que pagó el cliente: "EFECTIVO", "TARJETA", "TRANSFERENCIA"
    @Column(nullable = false)
    private String metodoPago;

    // Cliente que realizó la compra (Muchas ventas pueden ser al mismo cliente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Cajero o empleado responsable que registró la transacción
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // AQUÍ SE COLOCA LA RELACIÓN ONETOMANY:
    // Lista de artículos incluidos en este ticket. Al guardar la Venta se guardan todos sus detalles automáticamente.
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();
}