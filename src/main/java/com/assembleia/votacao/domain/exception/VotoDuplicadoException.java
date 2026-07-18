package com.assembleia.votacao.domain.exception;

import com.assembleia.votacao.domain.util.MascaraCpf;

public class VotoDuplicadoException extends ExcecaoNegocio {

    private final Long pautaId;
    private final String associadoId;

    public VotoDuplicadoException(Long pautaId, String associadoId) {
        super("O associado " + associadoId + " já votou na pauta " + pautaId);
        this.pautaId = pautaId;
        this.associadoId = associadoId;
    }

    @Override
    public String mensagemParaLog() {
        return "O associado " + MascaraCpf.mascarar(associadoId) + " já votou na pauta " + pautaId;
    }
}
