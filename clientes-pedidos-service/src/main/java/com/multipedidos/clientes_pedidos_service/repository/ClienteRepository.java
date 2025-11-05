package com.multipedidos.clientes_pedidos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.multipedidos.clientes_pedidos_service.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
