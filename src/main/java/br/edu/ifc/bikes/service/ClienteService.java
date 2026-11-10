package br.edu.ifc.bikes.service;

import br.edu.ifc.bikes.dto.ClienteRequestDTO;
import br.edu.ifc.bikes.dto.ClienteResponseDTO;
import br.edu.ifc.bikes.entity.Cliente;
import br.edu.ifc.bikes.entity.Usuario;
import br.edu.ifc.bikes.mapper.ClienteMapper;
import br.edu.ifc.bikes.repository.ClienteRepository;
import br.edu.ifc.bikes.repository.UsuarioRepository;
import br.edu.ifc.bikes.web.exception.RecursoNaoEncontradoException;
import br.edu.ifc.bikes.web.exception.UniqueViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteMapper mapper;

    /** O cadastro só é permitido para um usuário autenticado; o cliente é vinculado a ele. */
    @Transactional
    public ClienteResponseDTO create(ClienteRequestDTO dto, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado"));

        if (clienteRepository.existsByUsuarioId(usuario.getId())) {
            throw new UniqueViolationException("Este usuário já possui um cliente cadastrado");
        }

        String cpf = somenteDigitos(dto.cpf());
        if (clienteRepository.existsByCpf(cpf)) {
            throw new UniqueViolationException("CPF já cadastrado no sistema");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setCpf(cpf);
        cliente.setUsuario(usuario);

        try {
            return mapper.toResponse(clienteRepository.saveAndFlush(cliente));
        } catch (DataIntegrityViolationException e) { // corrida entre duas requisições
            throw new UniqueViolationException("CPF já cadastrado no sistema");
        }
    }

    /** Cliente comum só enxerga o próprio cadastro; ADMIN enxerga qualquer um. */
    @Transactional(readOnly = true)
    public ClienteResponseDTO getById(Long id, Authentication auth) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente id=" + id + " não encontrado"));

        if (!isAdmin(auth) && !cliente.getUsuario().getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("Cliente só pode consultar os próprios dados");
        }
        return mapper.toResponse(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO getMe(Authentication auth) {
        return clienteRepository.findByUsuarioEmail(auth.getName())
                .map(mapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Você ainda não possui cadastro de cliente"));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> getAll() { // restrito a ADMIN no controller
        return mapper.toResponse(clienteRepository.findAll());
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private String somenteDigitos(String cpf) {
        return cpf.replaceAll("\\D", "");
    }
}
