package com.umoar.posmilleniumgames.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "detalles_venta")
@Getter
@Setter
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Factura general a la que pertenece esta línea de cobro
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    // Videojuego, consola o accesorio que se está comprando
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Cuántas unidades del mismo producto lleva en esta línea (ej: 1, 2, 3...)
    @Column(nullable = false)
    private Integer cantidad;

    // Precio del juego al momento de la venta para no alterar tickets viejos si el precio sube después (ej: 69.99)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    // Cálculo monetario de la línea: cantidad * precioUnitario (ej: 2 * 69.99 = 139.98)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}