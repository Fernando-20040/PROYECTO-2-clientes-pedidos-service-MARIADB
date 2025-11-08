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

    public Pedido guardarPedido(PedidoDTO pedidoDTO) {
        if (pedidoDTO.getClienteId() == null || !clienteRepository.existsById(pedidoDTO.getClienteId())) {
            throw new IllegalArgumentException("El cliente con ID " + pedidoDTO.getClienteId() + " no existe");
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(pedidoDTO.getClienteId());

        // Guardamos los productos uno por uno y los asociamos
        List<Producto> productos = pedidoDTO.getProductos().stream()
                .map(dto -> {
                    Producto p = new Producto();
                    p.setNombre(dto.getNombre());
                    p.setPrecio(dto.getPrecio());
                    return productoRepository.save(p); // 🔹 ahora sí se guardan en BD
                })
                .collect(Collectors.toList());

        pedido.setProductos(productos);

        // Calcular el subtotal sumando precios
        double subtotal = productos.stream().mapToDouble(Producto::getPrecio).sum();
        double totalConIVA = OperacionesNegocio.calcularTotalConIVA(subtotal);
        pedido.setTotal(totalConIVA);

        return pedidoRepository.save(pedido);
        
        
    }
    
  
    
}
