package com.multipedidos.clientes_pedidos_service.service;

import com.multipedidos.clientes_pedidos_service.entity.Cliente;
import com.multipedidos.clientes_pedidos_service.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // 🔹 Listar todos los clientes
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // 🔹 Obtener un cliente por ID
    public Optional<Cliente> obtenerCliente(Long id) {
        return clienteRepository.findById(id);
    }

    // 🔹 Guardar un nuevo cliente
    public Cliente guardarCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (cliente.getCorreo() == null || cliente.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo del cliente es obligatorio");
        }
        if (clienteRepository.existsByCorreo(cliente.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un cliente con el correo " + cliente.getCorreo());
        }

        return clienteRepository.save(cliente);
    }

    // 🔹 Actualizar cliente existente
    public Cliente actualizarCliente(Long id, Cliente cliente) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente con ID " + id + " no encontrado"));

        if (cliente.getNombre() != null && !cliente.getNombre().isBlank()) {
            existente.setNombre(cliente.getNombre());
        }
        if (cliente.getCorreo() != null && !cliente.getCorreo().isBlank()) {
            existente.setCorreo(cliente.getCorreo());
        }
        if (cliente.getTelefono() != null) {
            existente.setTelefono(cliente.getTelefono());
        }
        if (cliente.getDireccion() != null) {
            existente.setDireccion(cliente.getDireccion());
        }

        return clienteRepository.save(existente);
    }

    // 🔹 Eliminar cliente
    public void eliminarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("El cliente con ID " + id + " no existe");
        }
        clienteRepository.deleteById(id);
    }
}
