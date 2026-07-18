package com.assembleia.votacao.domain.exception;

public abstract class ExcecaoNegocio extends RuntimeException {

    protected ExcecaoNegocio(String mensagem) {
        super(mensagem);
    }
}
