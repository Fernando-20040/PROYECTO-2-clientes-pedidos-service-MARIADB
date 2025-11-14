package com.multipedidos.clientes_pedidos_service.dto;

import lombok.Data;

@Data
public class ProductoDTO {
    private String nombre;
    private double precio;
    private int cantidad;
}
