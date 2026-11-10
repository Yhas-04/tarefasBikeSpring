package br.edu.ifc.bikes.dto;

import br.edu.ifc.bikes.entity.StatusBicicleta;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record BicicletaRequestDTO(
        @NotBlank(message = "A marca é obrigatória") @Size(max = 50) String marca,
        @NotBlank(message = "O modelo é obrigatório") @Size(max = 50) String modelo,
        @NotBlank(message = "A cor é obrigatória") @Size(max = 30) String cor,
        @NotNull(message = "O aro é obrigatório") @Min(value = 12, message = "Aro mínimo: 12") @Max(value = 29, message = "Aro máximo: 29") Integer aro,
        @NotBlank(message = "O número de série é obrigatório") @Size(min = 5, max = 30) String numeroSerie,
        @NotNull(message = "O valor por hora é obrigatório") @Positive(message = "O valor por hora deve ser positivo")
        @Digits(integer = 8, fraction = 2) BigDecimal valorHora,
        StatusBicicleta status // opcional: se nulo, assume DISPONIVEL na criação
) {}
