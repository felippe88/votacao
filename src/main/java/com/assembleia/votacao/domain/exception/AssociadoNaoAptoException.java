package com.assembleia.votacao.domain.exception;

import com.assembleia.votacao.domain.util.MascaraCpf;

public class AssociadoNaoAptoException extends ExcecaoNegocio {

    private final String cpf;

    public AssociadoNaoAptoException(String cpf) {
        super("Associado " + cpf + " não está apto a votar no momento");
        this.cpf = cpf;
    }

    @Override
    public String mensagemParaLog() {
        return "Associado " + MascaraCpf.mascarar(cpf) + " não está apto a votar no momento";
    }
}
