package com.assembleia.votacao.domain.exception;

public class ServicoVerificacaoIndisponivelException extends ExcecaoNegocio {

    public ServicoVerificacaoIndisponivelException(String cpf, Throwable causa) {
        super("Não foi possível verificar a elegibilidade do associado " + cpf + " para votar", causa);
    }
}
