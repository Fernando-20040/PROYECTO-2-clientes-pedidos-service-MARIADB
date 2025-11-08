package com.multipedidos.clientes_pedidos_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class PedidoDTO {
    private Long clienteId;
    private List<ProductoDTO> productos;
    private String nombre;

}
