package com.multipedidos.clientes_pedidos_service.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.multipedidos.clientes_pedidos_service.entity.Pedido;
import com.multipedidos.clientes_pedidos_service.repository.PedidoRepository;
import com.multipedidos.clientes_pedidos_service.repository.ClienteRepository;
import com.multipedidos.common.OperacionesNegocio;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPedido(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido guardarPedido(Pedido pedido) {
        // ✅ Validar cliente existente
        if (pedido.getClienteId() == null || !clienteRepository.existsById(pedido.getClienteId())) {
            throw new IllegalArgumentException("El cliente con ID " + pedido.getClienteId() + " no existe");
        }

        // ✅ Evitar null en lista de productos
        double subtotal = (pedido.getProductos() != null)
                ? pedido.getProductos().stream().mapToDouble(p -> p.getPrecio()).sum()
                : 0.0;

        double totalConIVA = OperacionesNegocio.calcularTotalConIVA(subtotal);
        pedido.setTotal(totalConIVA);

        return pedidoRepository.save(pedido);
    }

    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }
}
