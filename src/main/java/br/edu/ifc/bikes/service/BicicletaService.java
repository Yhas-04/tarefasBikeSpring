package br.edu.ifc.bikes.service;

import br.edu.ifc.bikes.dto.BicicletaRequestDTO;
import br.edu.ifc.bikes.dto.BicicletaResponseDTO;
import br.edu.ifc.bikes.entity.Bicicleta;
import br.edu.ifc.bikes.entity.StatusBicicleta;
import br.edu.ifc.bikes.mapper.BicicletaMapper;
import br.edu.ifc.bikes.repository.BicicletaRepository;
import br.edu.ifc.bikes.web.exception.RecursoNaoEncontradoException;
import br.edu.ifc.bikes.web.exception.UniqueViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BicicletaService {

    private final BicicletaRepository repository;
    private final BicicletaMapper mapper;

    @Transactional
    public BicicletaResponseDTO create(BicicletaRequestDTO dto) {
        String serie = dto.numeroSerie().trim().toUpperCase();
        if (repository.existsByNumeroSerie(serie)) {
            throw new UniqueViolationException("Número de série " + serie + " já cadastrado");
        }
        try {
            return mapper.toResponse(repository.saveAndFlush(mapper.toEntity(dto)));
        } catch (DataIntegrityViolationException e) {
            throw new UniqueViolationException("Número de série " + serie + " já cadastrado");
        }
    }

    @Transactional(readOnly = true)
    public BicicletaResponseDTO getById(Long id) {
        return mapper.toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public List<BicicletaResponseDTO> getAll(StatusBicicleta status) {
        return mapper.toResponse(status == null ? repository.findAll() : repository.findByStatus(status));
    }

    @Transactional
    public BicicletaResponseDTO update(Long id, BicicletaRequestDTO dto) {
        Bicicleta b = find(id);
        String serie = dto.numeroSerie().trim().toUpperCase();
        if (repository.existsByNumeroSerieAndIdNot(serie, id)) {
            throw new UniqueViolationException("Número de série " + serie + " já cadastrado");
        }
        mapper.updateEntity(b, dto);
        return mapper.toResponse(repository.saveAndFlush(b));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    private Bicicleta find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bicicleta id=" + id + " não encontrada"));
    }
}
