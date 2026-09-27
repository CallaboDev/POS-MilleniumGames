package com.umoar.posmilleniumgames.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Getter
@Setter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código de barras escaneado o identificador único (ej: "045496590437", "PS5-GOW-01")
    @Column(unique = true, nullable = false)
    private String sku;

    // Título del juego o producto (ej: "The Legend of Zelda: Tears of the Kingdom", "Control DualSense Blanco")
    @Column(nullable = false)
    private String nombre;

    // Detalles adicionales (ej: "Edición estándar en cartucho con idioma en español")
    private String descripcion;

    // Precio de venta en dólares (ej: 69.99, 499.99, 24.50)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    // Cantidad disponible en tienda o bodega (ej: 15, 8, 0 si se agota)
    @Column(nullable = false)
    private Integer stock;

    // Clasificación del artículo (ej: Vinculado a Categoría "Videojuegos" o "Accesorios")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Consola para la que aplica (ej: Vinculado a Plataforma "PlayStation 5" o "Nintendo Switch")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plataforma_id")
    private Plataforma plataforma;

    // Nombre de la imagen subida al crear la venta
    private String imagen;
}