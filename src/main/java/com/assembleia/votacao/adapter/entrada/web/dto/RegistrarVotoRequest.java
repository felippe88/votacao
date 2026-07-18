package com.assembleia.votacao.adapter.entrada.web.dto;

import com.assembleia.votacao.domain.model.OpcaoVoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarVotoRequest(@NotBlank String associadoId, @NotNull OpcaoVoto opcao) {
}
