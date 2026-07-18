package com.assembleia.votacao.domain.exception;

public class VotoDuplicadoException extends ExcecaoNegocio {

    public VotoDuplicadoException(Long pautaId, String associadoId) {
        super("O associado " + associadoId + " já votou na pauta " + pautaId);
    }
}
