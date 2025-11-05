package com.multipedidos.clientes_pedidos_service.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.multipedidos.clientes_pedidos_service.entity.Cliente;
import com.multipedidos.clientes_pedidos_service.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerCliente(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}
