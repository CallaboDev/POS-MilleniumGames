package com.umoar.posmilleniumgames.modulos.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Datos requeridos para registrar o actualizar un producto")
public class ProductoRequestDTO {

    @Schema(description = "Código de barras o identificador único (SKU)", example = "PS5-GOW-RAGNAROK", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sku;

    @Schema(description = "Nombre o título comercial del producto", example = "God of War Ragnarök", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Schema(description = "Descripción detallada del artículo", example = "Edición estándar física en disco para consola PlayStation 5")
    private String descripcion;

    @Schema(description = "Precio de venta al público en USD", example = "69.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal precio;

    @Schema(description = "Cantidad de unidades disponibles en stock", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stock;

    @Schema(description = "ID de la categoría a la que pertenece", example = "1")
    private Long categoriaId;

    @Schema(description = "ID de la plataforma/consola a la que pertenece", example = "2")
    private Long plataformaId;
}
