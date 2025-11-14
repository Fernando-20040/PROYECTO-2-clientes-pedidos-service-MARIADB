package com.multipedidos.clientes_pedidos_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class PedidoDTO {

    private Long clienteId;
    private String nombre;
    private List<ProductoDTO> productos;

    // 🔹 Desglose adicional (generalmente lo calcula el backend)
    private double subtotal;
    private double iva;
    private double descuentoPorcentaje;
    private double descuento;
    private double total;

    private String estado; // opcional

    @Data
    public static class ProductoDTO {
        private String nombre;
        private double precio;
        private int cantidad; // 🆕 nueva propiedad
    }
}
