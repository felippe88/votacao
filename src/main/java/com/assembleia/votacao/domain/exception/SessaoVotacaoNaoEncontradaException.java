package com.assembleia.votacao.domain.exception;

public class SessaoVotacaoNaoEncontradaException extends ExcecaoNegocio {

    public SessaoVotacaoNaoEncontradaException(Long pautaId) {
        super("Sessão de votação não encontrada para a pauta: " + pautaId);
    }
}
