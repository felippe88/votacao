package com.assembleia.votacao.domain.exception;

public class AssociadoNaoAptoException extends ExcecaoNegocio {

    public AssociadoNaoAptoException(String cpf) {
        super("Associado " + cpf + " não está apto a votar no momento");
    }
}
