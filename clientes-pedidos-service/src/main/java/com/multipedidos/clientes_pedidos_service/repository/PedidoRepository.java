package com.multipedidos.clientes_pedidos_service.repository;

import com.multipedidos.clientes_pedidos_service.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByClienteIdAndEstado(Long clienteId, String estado);

    // 🆕 Buscar varios pedidos por lista de IDs (útil para facturas)
    List<Pedido> findByIdIn(List<Long> ids);
}
