package com.assembleia.votacao.domain.model;

public record ResultadoApuracao(Long pautaId, String tituloPauta, long totalSim, long totalNao,
                                 ResultadoFinal resultado) {
}
