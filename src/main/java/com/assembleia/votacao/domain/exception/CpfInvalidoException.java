package com.assembleia.votacao.domain.exception;

public class CpfInvalidoException extends ExcecaoNegocio {

    public CpfInvalidoException(String cpf) {
        super("CPF " + cpf + " inválido ou não encontrado");
    }
}
