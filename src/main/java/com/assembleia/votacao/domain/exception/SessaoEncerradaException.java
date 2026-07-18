package com.assembleia.votacao.domain.exception;

public class SessaoEncerradaException extends ExcecaoNegocio {

    public SessaoEncerradaException(Long pautaId) {
        super("A sessão de votação da pauta " + pautaId + " está encerrada ou não foi aberta");
    }
}
