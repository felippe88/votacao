package com.assembleia.votacao.domain.port.entrada;

import com.assembleia.votacao.domain.model.SessaoVotacao;

public interface AbrirSessaoVotacaoUseCase {

    SessaoVotacao abrir(Long pautaId, Long duracaoSegundos);
}
