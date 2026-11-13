package com.umoar.posmilleniumgames.modulos.inventario.dto;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta detallada con información de un producto")
public class ProductoResponseDTO {

    @Schema(description = "Identificador único generado en base de datos", example = "1")
    private Long id;

    @Schema(description = "Código de barras o SKU", example = "PS5-GOW-RAGNAROK")
    private String sku;

    @Schema(description = "Nombre o título comercial del producto", example = "God of War Ragnarök")
    private String nombre;

    @Schema(description = "Descripción del producto", example = "Edición estándar física en disco para consola PlayStation 5")
    private String descripcion;

    @Schema(description = "Precio de venta en USD", example = "69.99")
    private BigDecimal precio;

    @Schema(description = "Unidades actuales en existencia", example = "25")
    private Integer stock;

    @Schema(description = "Nombre de la categoría asociada", example = "Videojuegos")
    private String categoria;

    @Schema(description = "Nombre de la plataforma asociada", example = "PlayStation 5")
    private String plataforma;

    @Schema(description = "Ruta o nombre del archivo de imagen", example = "gow_ragnarok_cover.jpg")
    private String imagen;

    public static ProductoResponseDTO fromEntity(Producto producto) {
        if (producto == null) {
            return null;
        }
        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .sku(producto.getSku())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .categoria(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null)
                .plataforma(producto.getPlataforma() != null ? producto.getPlataforma().getNombre() : null)
                .imagen(producto.getImagen())
                .build();
    }
}
