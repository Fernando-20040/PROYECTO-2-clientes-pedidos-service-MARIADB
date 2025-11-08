package com.multipedidos.clientes_pedidos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.multipedidos.clientes_pedidos_service.entity.Pedido;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
}
