package com.assembleia.votacao.domain.model;

import java.time.Instant;

public record Voto(Long id, Long pautaId, String associadoId, OpcaoVoto opcao, Instant dataVoto) {
}
