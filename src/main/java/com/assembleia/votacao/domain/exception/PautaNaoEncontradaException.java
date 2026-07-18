package com.assembleia.votacao.domain.exception;

public class PautaNaoEncontradaException extends ExcecaoNegocio {

    public PautaNaoEncontradaException(Long pautaId) {
        super("Pauta não encontrada: " + pautaId);
    }
}
