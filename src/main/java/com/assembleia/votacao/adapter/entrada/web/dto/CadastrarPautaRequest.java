package com.assembleia.votacao.adapter.entrada.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CadastrarPautaRequest(@NotBlank String titulo, String descricao) {
}
