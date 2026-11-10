package br.edu.ifc.bikes.mapper;

import br.edu.ifc.bikes.dto.ClienteResponseDTO;
import br.edu.ifc.bikes.entity.Cliente;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClienteMapper {

    public ClienteResponseDTO toResponse(Cliente c) {
        return new ClienteResponseDTO(
                c.getId(), c.getNome(), c.getCpf(),
                c.getUsuario().getId(),
                c.getDataCriacao(), c.getDataModificacao());
    }

    public List<ClienteResponseDTO> toResponse(List<Cliente> clientes) {
        return clientes.stream().map(this::toResponse).toList();
    }
}
