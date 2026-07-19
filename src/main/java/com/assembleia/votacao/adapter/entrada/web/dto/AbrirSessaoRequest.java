package com.assembleia.votacao.adapter.entrada.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

public record AbrirSessaoRequest(
        @Positive
        @Max(value = 31_536_000, message = "duracaoSegundos não pode passar de 31536000 (1 ano)")
        Long duracaoSegundos) {
}
