package com.assembleia.votacao.domain.exception;

import com.assembleia.votacao.domain.util.MascaraCpf;

public class ServicoVerificacaoIndisponivelException extends ExcecaoNegocio {

    private final String cpf;

    public ServicoVerificacaoIndisponivelException(String cpf, Throwable causa) {
        super("Não foi possível verificar a elegibilidade do associado " + cpf + " para votar", causa);
        this.cpf = cpf;
    }

    @Override
    public String mensagemParaLog() {
        return "Não foi possível verificar a elegibilidade do associado " + MascaraCpf.mascarar(cpf) + " para votar";
    }
}
