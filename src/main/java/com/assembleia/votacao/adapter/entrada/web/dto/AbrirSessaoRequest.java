package com.assembleia.votacao.adapter.entrada.web.dto;

import jakarta.validation.constraints.Positive;

public record AbrirSessaoRequest(@Positive Long duracaoSegundos) {
}
