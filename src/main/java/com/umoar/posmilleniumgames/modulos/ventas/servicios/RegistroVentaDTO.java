package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RegistroVentaDTO {
    private Long clienteId;
    private String metodoPago;
    private List<ItemVentaDTO> items = new ArrayList<>();
    private BigDecimal montoRecibido;
    private BigDecimal cambio;
}
