package com.assembleia.votacao.domain.port.saida;

import com.assembleia.votacao.domain.model.SessaoVotacao;

import java.util.Optional;

public interface SessaoVotacaoRepositorio {

    SessaoVotacao salvar(SessaoVotacao sessaoVotacao);

    Optional<SessaoVotacao> buscarPorPautaId(Long pautaId);

    boolean existePorPautaId(Long pautaId);
}
