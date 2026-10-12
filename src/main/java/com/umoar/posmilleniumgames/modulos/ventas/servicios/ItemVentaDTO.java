package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVentaDTO {
    private Long productoId;
    private Integer cantidad;
}
