package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Usuario;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Venta;

import java.util.List;
import java.util.Optional;

public interface VentaService {
    Venta procesarVenta(RegistroVentaDTO registroVentaDTO, Usuario usuario);
    Venta obtenerPorId(Long id);
    Optional<Venta> buscarPorId(Long id);
    Optional<Venta> buscarPorNumeroFactura(String numeroFactura);
    String generarNumeroFactura();
    List<Venta> listarTodas();
}
