package br.edu.ifc.bikes.dto;

import br.edu.ifc.bikes.entity.StatusBicicleta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BicicletaResponseDTO(
        Long id, String marca, String modelo, String cor, Integer aro,
        String numeroSerie, BigDecimal valorHora, StatusBicicleta status,
        LocalDateTime dataCriacao, LocalDateTime dataModificacao
) {}
