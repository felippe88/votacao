package com.assembleia.votacao.domain.port.entrada;

import com.assembleia.votacao.domain.model.Pauta;

import java.util.List;

public interface ListarPautasUseCase {

    List<Pauta> listar();
}
