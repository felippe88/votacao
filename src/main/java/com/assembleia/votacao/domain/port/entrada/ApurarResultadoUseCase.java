package com.assembleia.votacao.domain.port.entrada;

import com.assembleia.votacao.domain.model.ResultadoApuracao;

public interface ApurarResultadoUseCase {

    ResultadoApuracao apurar(Long pautaId);
}
