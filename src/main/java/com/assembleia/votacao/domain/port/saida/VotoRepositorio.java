package com.assembleia.votacao.domain.port.saida;

import com.assembleia.votacao.domain.model.OpcaoVoto;
import com.assembleia.votacao.domain.model.Voto;

public interface VotoRepositorio {

    Voto salvar(Voto voto);

    long contarPorPautaIdEOpcao(Long pautaId, OpcaoVoto opcao);
}
