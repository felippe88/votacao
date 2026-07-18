package com.assembleia.votacao.domain.model;

import java.time.Instant;

public record SessaoVotacao(Long id, Long pautaId, Instant abertura, Instant fechamento) {

    public boolean estaAberta(Instant referencia) {
        return !referencia.isBefore(abertura) && referencia.isBefore(fechamento);
    }
}
