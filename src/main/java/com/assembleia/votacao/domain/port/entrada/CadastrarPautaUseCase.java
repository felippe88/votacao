package com.assembleia.votacao.domain.port.entrada;

import com.assembleia.votacao.domain.model.Pauta;

public interface CadastrarPautaUseCase {

    Pauta cadastrar(String titulo, String descricao);
}
