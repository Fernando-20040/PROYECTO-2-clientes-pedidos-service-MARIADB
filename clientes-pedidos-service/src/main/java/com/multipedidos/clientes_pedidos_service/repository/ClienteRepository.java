package com.multipedidos.clientes_pedidos_service.repository;

import com.multipedidos.clientes_pedidos_service.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCorreo(String correo);
}
