package br.edu.ifc.bikes.mapper;

import br.edu.ifc.bikes.dto.BicicletaRequestDTO;
import br.edu.ifc.bikes.dto.BicicletaResponseDTO;
import br.edu.ifc.bikes.entity.Bicicleta;
import br.edu.ifc.bikes.entity.StatusBicicleta;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BicicletaMapper {

    public Bicicleta toEntity(BicicletaRequestDTO dto) {
        Bicicleta b = new Bicicleta();
        updateEntity(b, dto);
        return b;
    }

    public void updateEntity(Bicicleta b, BicicletaRequestDTO dto) {
        b.setMarca(dto.marca());
        b.setModelo(dto.modelo());
        b.setCor(dto.cor());
        b.setAro(dto.aro());
        b.setNumeroSerie(dto.numeroSerie().trim().toUpperCase());
        b.setValorHora(dto.valorHora());
        if (dto.status() != null) {
            b.setStatus(dto.status());
        } else if (b.getStatus() == null) {
            b.setStatus(StatusBicicleta.DISPONIVEL);
        }
    }

    public BicicletaResponseDTO toResponse(Bicicleta b) {
        return new BicicletaResponseDTO(b.getId(), b.getMarca(), b.getModelo(), b.getCor(), b.getAro(),
                b.getNumeroSerie(), b.getValorHora(), b.getStatus(), b.getDataCriacao(), b.getDataModificacao());
    }

    public List<BicicletaResponseDTO> toResponse(List<Bicicleta> list) {
        return list.stream().map(this::toResponse).toList();
    }
}
