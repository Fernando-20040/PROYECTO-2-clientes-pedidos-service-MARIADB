package com.multipedidos.clientes_pedidos_service.service;

import com.multipedidos.clientes_pedidos_service.dto.PedidoDTO;
import com.multipedidos.clientes_pedidos_service.entity.Pedido;
import com.multipedidos.clientes_pedidos_service.entity.Producto;
import com.multipedidos.clientes_pedidos_service.repository.ClienteRepository;
import com.multipedidos.clientes_pedidos_service.repository.PedidoRepository;
import com.multipedidos.clientes_pedidos_service.repository.ProductoRepository;
import com.multipedidos.common.OperacionesNegocio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProductoRepository productoRepository,
                         ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
    }

    /* ========= LISTADOS ========= */

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    // 🆕 Solo pedidos PENDIENTES (para facturas)
    public List<Pedido> listarPedidosPendientesPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdAndEstado(clienteId, "PENDIENTE");
    }

    // 🆕 Obtener pedido por ID
    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pedido no encontrado con ID " + id
                ));
    }

    /* ========= CREAR ========= */

    public Pedido guardarPedido(PedidoDTO pedidoDTO) {
        if (pedidoDTO.getClienteId() == null ||
                !clienteRepository.existsById(pedidoDTO.getClienteId())) {
            throw new IllegalArgumentException(
                    "El cliente con ID " + pedidoDTO.getClienteId() + " no existe"
            );
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(pedidoDTO.getClienteId());
        pedido.setNombre(pedidoDTO.getNombre());

        // Guardar productos con cantidad
        List<Producto> productos = pedidoDTO.getProductos().stream()
                .map(dto -> {
                    Producto p = new Producto();
                    p.setNombre(dto.getNombre());
                    p.setPrecio(dto.getPrecio());

                    // 👇 si no envían cantidad o es 0, tomamos 1 para compatibilidad
                    int cantidad = dto.getCantidad() > 0 ? dto.getCantidad() : 1;
                    p.setCantidad(cantidad);

                    return productoRepository.save(p);
                })
                .collect(Collectors.toList());

        pedido.setProductos(productos);

        // Cálculos contables usando precio * cantidad
        double subtotal = productos.stream()
                .mapToDouble(p ->
                        p.getPrecio() *
                        (p.getCantidad() > 0 ? p.getCantidad() : 1)
                )
                .sum();

        double iva = OperacionesNegocio.calcularIVA(subtotal);
        double totalConIVA = OperacionesNegocio.calcularTotalConIVA(subtotal);

        double porcentajeDescuento =
                pedidoDTO.getDescuentoPorcentaje() > 0
                        ? pedidoDTO.getDescuentoPorcentaje()
                        : 0.0;

        double totalConDescuento =
                OperacionesNegocio.aplicarDescuento(totalConIVA, porcentajeDescuento);

        double descuento = totalConIVA - totalConDescuento;

        pedido.setSubtotal(subtotal);
        pedido.setIva(iva);
        pedido.setDescuentoPorcentaje(porcentajeDescuento);
        pedido.setDescuento(descuento);
        pedido.setTotal(totalConDescuento);
        pedido.setEstado("PENDIENTE");

        return pedidoRepository.save(pedido);
    }

    /* ========= ESTADO ========= */

    // Cambiar estado (PENDIENTE, FACTURADO, ANULADO)
    public Pedido cambiarEstado(Long id, String estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pedido no encontrado con ID " + id
                ));

        estado = estado.toUpperCase();
        if (!List.of("PENDIENTE", "FACTURADO", "ANULADO").contains(estado)) {
            throw new IllegalArgumentException("Estado inválido: " + estado);
        }

        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }

    /* ========= ELIMINAR ========= */

    // 🗑️ Eliminar pedido
    public void eliminarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pedido no encontrado con ID " + id
                ));

        // Regla de negocio opcional: no permitir eliminar FACTURADO
        if ("FACTURADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException(
                    "No se puede eliminar un pedido FACTURADO. Anule la factura primero."
            );
        }

        // Limpiar relaciones ManyToMany para evitar problemas en tabla intermedia
        if (pedido.getProductos() != null && !pedido.getProductos().isEmpty()) {
            pedido.getProductos().clear();
            pedidoRepository.save(pedido);
        }

        pedidoRepository.deleteById(id);
    }
}
