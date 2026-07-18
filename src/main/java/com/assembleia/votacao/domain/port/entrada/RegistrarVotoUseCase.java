package com.assembleia.votacao.domain.port.entrada;

import com.assembleia.votacao.domain.model.OpcaoVoto;
import com.assembleia.votacao.domain.model.Voto;

public interface RegistrarVotoUseCase {

    Voto registrar(Long pautaId, String associadoId, OpcaoVoto opcao);
}
