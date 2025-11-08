package com.multipedidos.clientes_pedidos_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class PedidoDTO {
    private Long clienteId;
    private String nombre;
    private List<ProductoDTO> productos;

    // 🔹 Nuevo campo opcional para admitir actualizaciones de estado (si se usa en el futuro)
    private String estado; // puede venir vacío, no afecta si no se usa

    @Data
    public static class ProductoDTO {
        private String nombre;
        private double precio;
    }
}
