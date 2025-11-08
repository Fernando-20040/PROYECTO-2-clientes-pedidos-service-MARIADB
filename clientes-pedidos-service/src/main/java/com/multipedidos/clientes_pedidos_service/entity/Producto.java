package com.multipedidos.clientes_pedidos_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "producto")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double precio;

    @ManyToMany(mappedBy = "productos")
    @JsonBackReference
    @JsonIgnore
    private List<Pedido> pedidos = new ArrayList<>();
}
