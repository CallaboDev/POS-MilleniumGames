package com.umoar.posmilleniumgames.modulos.ventas.servicios;

import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import com.umoar.posmilleniumgames.modulos.usuarios.modelos.Usuario;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Cliente;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.DetalleVenta;
import com.umoar.posmilleniumgames.modulos.ventas.modelos.Venta;
import com.umoar.posmilleniumgames.modulos.ventas.repositorios.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoService productoService;
    private final ClienteService clienteService;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            ProductoService productoService,
                            ClienteService clienteService) {
        this.ventaRepository = ventaRepository;
        this.productoService = productoService;
        this.clienteService = clienteService;
    }

    @Override
    @Transactional
    public Venta procesarVenta(RegistroVentaDTO registroVentaDTO, Usuario usuario) {
        if (registroVentaDTO == null || registroVentaDTO.getItems() == null || registroVentaDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacío para procesar una venta.");
        }
        if (usuario == null) {
            throw new IllegalStateException("Se requiere un usuario autenticado para registrar la venta.");
        }

        // 1. Asignar cliente o Consumidor Final por defecto
        Cliente cliente;
        if (registroVentaDTO.getClienteId() != null && registroVentaDTO.getClienteId() > 0) {
            cliente = clienteService.buscarPorId(registroVentaDTO.getClienteId())
                    .orElseGet(clienteService::obtenerOCrearConsumidorFinal);
        } else {
            cliente = clienteService.obtenerOCrearConsumidorFinal();
        }

        // 2. Método de pago
        String metodoPago = registroVentaDTO.getMetodoPago();
        if (metodoPago == null || metodoPago.isBlank()) {
            metodoPago = "EFECTIVO";
        } else {
            metodoPago = metodoPago.trim().toUpperCase();
        }

        // 3. Crear cabecera de la Venta
        Venta venta = new Venta();
        venta.setNumeroFactura(generarNumeroFactura());
        venta.setFechaHora(LocalDateTime.now());
        venta.setMetodoPago(metodoPago);
        venta.setCliente(cliente);
        venta.setUsuario(usuario);

        BigDecimal total = BigDecimal.ZERO;

        // 4. Procesar y validar cada ítem del carrito
        for (ItemVentaDTO item : registroVentaDTO.getItems()) {
            if (item.getProductoId() == null) {
                throw new IllegalArgumentException("El identificador del producto es requerido.");
            }
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a cero.");
            }

            Producto producto = productoService.obtenerPorId(item.getProductoId());

            // Validación de existencia en stock
            if (producto.getStock() < item.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre()
                        + " (Disponibles: " + producto.getStock() + ", Solicitados: " + item.getCantidad() + ")");
            }

            // Descontar existencias físicas en inventario
            productoService.descontarStock(producto.getId(), item.getCantidad());

            // Cálculo matemático de subtotales
            BigDecimal precioUnitario = producto.getPrecio();
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);

            venta.getDetalles().add(detalle);
            total = total.add(subtotal);
        }

        venta.setTotal(total);

        // 5. Persistir venta con detalles (CascadeType.ALL)
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Venta obtenerPorId(Long id) {
        return ventaRepository.buscarPorIdConDetalles(id)
                .orElseGet(() -> ventaRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con ID: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> buscarPorId(Long id) {
        return ventaRepository.buscarPorIdConDetalles(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> buscarPorNumeroFactura(String numeroFactura) {
        return ventaRepository.findByNumeroFactura(numeroFactura);
    }

    @Override
    @Transactional(readOnly = true)
    public String generarNumeroFactura() {
        long consecutivo = ventaRepository.findTopByOrderByIdDesc()
                .map(v -> v.getId() + 1)
                .orElse(1L);
        String numero = String.format("FAC-%05d", consecutivo);
        while (ventaRepository.existsByNumeroFactura(numero)) {
            consecutivo++;
            numero = String.format("FAC-%05d", consecutivo);
        }
        return numero;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }
}
