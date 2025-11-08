package com.multipedidos.clientes_pedidos_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "pedido")
@Data
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;

    private double total;

    @ManyToMany
    @JoinTable(
        name = "pedido_producto",
        joinColumns = @JoinColumn(name = "pedido_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos;

    // 🔹 Nuevo campo de estado
    @Column(nullable = false)
    private String estado = "PENDIENTE"; // Valores: PENDIENTE, FACTURADO, ANULADO
}
