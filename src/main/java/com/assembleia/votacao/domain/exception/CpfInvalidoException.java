package com.assembleia.votacao.domain.exception;

import com.assembleia.votacao.domain.util.MascaraCpf;

public class CpfInvalidoException extends ExcecaoNegocio {

    private final String cpf;

    public CpfInvalidoException(String cpf) {
        super("CPF " + cpf + " inválido ou não encontrado");
        this.cpf = cpf;
    }

    @Override
    public String mensagemParaLog() {
        return "CPF " + MascaraCpf.mascarar(cpf) + " inválido ou não encontrado";
    }
}
