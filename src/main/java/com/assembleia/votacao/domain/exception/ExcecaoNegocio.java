package com.assembleia.votacao.domain.exception;

public abstract class ExcecaoNegocio extends RuntimeException {

    protected ExcecaoNegocio(String mensagem) {
        super(mensagem);
    }

    protected ExcecaoNegocio(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public String mensagemParaLog() {
        return getMessage();
    }
}
