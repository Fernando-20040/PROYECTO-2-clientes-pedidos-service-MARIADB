package com.multipedidos.clientes_pedidos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.multipedidos.clientes_pedidos_service.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {}
