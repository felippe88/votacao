package com.assembleia.votacao.domain.port.saida;

import com.assembleia.votacao.domain.model.Pauta;

import java.util.List;
import java.util.Optional;

public interface PautaRepositorio {

    Pauta salvar(Pauta pauta);

    Optional<Pauta> buscarPorId(Long id);

    List<Pauta> listarTodas();
}
