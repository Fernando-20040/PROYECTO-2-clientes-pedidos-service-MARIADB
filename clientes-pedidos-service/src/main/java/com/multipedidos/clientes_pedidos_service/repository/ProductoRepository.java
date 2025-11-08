package com.multipedidos.clientes_pedidos_service.repository;

import com.multipedidos.clientes_pedidos_service.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
