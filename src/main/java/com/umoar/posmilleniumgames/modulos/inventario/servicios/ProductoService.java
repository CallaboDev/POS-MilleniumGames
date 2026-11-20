package com.umoar.posmilleniumgames.modulos.inventario.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;

import java.util.List;

public interface ProductoService {

    List<Producto> obtenerTodos();

    Producto obtenerPorId(Long id);

    List<Producto> buscarPorNombre(String nombre);

    Producto buscarPorSku(String sku);

    Producto guardar(Producto producto);

    void eliminar(Long id);

    boolean hayStockDisponible(Long id, int cantidad);

    void descontarStock(Long id, int cantidad);
}