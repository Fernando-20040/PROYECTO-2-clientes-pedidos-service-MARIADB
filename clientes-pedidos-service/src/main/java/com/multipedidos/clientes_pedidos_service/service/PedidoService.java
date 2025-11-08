package com.multipedidos.clientes_pedidos_service.service;

import com.multipedidos.clientes_pedidos_service.dto.*;
import com.multipedidos.clientes_pedidos_service.entity.*;
import com.multipedidos.clientes_pedidos_service.repository.*;
import com.multipedidos.common.OperacionesNegocio;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository, ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    // 🔹 Crear pedido
    public Pedido guardarPedido(PedidoDTO pedidoDTO) {
        if (pedidoDTO.getClienteId() == null || !clienteRepository.existsById(pedidoDTO.getClienteId())) {
            throw new IllegalArgumentException("El cliente con ID " + pedidoDTO.getClienteId() + " no existe");
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(pedidoDTO.getClienteId());

        List<Producto> productos = pedidoDTO.getProductos().stream()
                .map(dto -> {
                    Producto p = new Producto();
                    p.setNombre(dto.getNombre());
                    p.setPrecio(dto.getPrecio());
                    return productoRepository.save(p);
                })
                .collect(Collectors.toList());

        pedido.setProductos(productos);

        double subtotal = productos.stream().mapToDouble(Producto::getPrecio).sum();
        double totalConIVA = OperacionesNegocio.calcularTotalConIVA(subtotal);
        pedido.setTotal(totalConIVA);
        pedido.setEstado("PENDIENTE");

        return pedidoRepository.save(pedido);
    }

    // 🔹 Actualizar pedido (solo si está pendiente)
    public Pedido actualizarPedido(Long id, PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        if ("FACTURADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException("No se puede editar un pedido ya facturado");
        }

        if (pedidoDTO.getClienteId() != null && clienteRepository.existsById(pedidoDTO.getClienteId())) {
            pedido.setClienteId(pedidoDTO.getClienteId());
        }

        if (pedidoDTO.getProductos() != null && !pedidoDTO.getProductos().isEmpty()) {
            List<Producto> nuevosProductos = pedidoDTO.getProductos().stream()
                    .map(dto -> {
                        Producto p = new Producto();
                        p.setNombre(dto.getNombre());
                        p.setPrecio(dto.getPrecio());
                        return productoRepository.save(p);
                    })
                    .collect(Collectors.toList());
            pedido.setProductos(nuevosProductos);

            double subtotal = nuevosProductos.stream().mapToDouble(Producto::getPrecio).sum();
            double totalConIVA = OperacionesNegocio.calcularTotalConIVA(subtotal);
            pedido.setTotal(totalConIVA);
        }

        return pedidoRepository.save(pedido);
    }

    // 🔹 Cambiar estado (usado al facturar)
    public Pedido cambiarEstado(Long id, String estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        estado = estado.toUpperCase();
        if (!List.of("PENDIENTE", "FACTURADO", "ANULADO").contains(estado)) {
            throw new IllegalArgumentException("Estado inválido: " + estado);
        }

        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }
}
