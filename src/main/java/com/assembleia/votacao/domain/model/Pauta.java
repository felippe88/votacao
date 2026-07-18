package com.assembleia.votacao.domain.model;

import java.time.Instant;

public record Pauta(Long id, String titulo, String descricao, Instant dataCriacao) {
}
