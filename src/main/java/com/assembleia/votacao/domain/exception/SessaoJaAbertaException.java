package com.assembleia.votacao.domain.exception;

public class SessaoJaAbertaException extends ExcecaoNegocio {

    public SessaoJaAbertaException(Long pautaId) {
        super("Já existe uma sessão de votação para a pauta: " + pautaId);
    }
}
