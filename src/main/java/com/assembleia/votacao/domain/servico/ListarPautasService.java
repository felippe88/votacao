package com.assembleia.votacao.domain.servico;

import com.assembleia.votacao.domain.model.Pauta;
import com.assembleia.votacao.domain.port.entrada.ListarPautasUseCase;
import com.assembleia.votacao.domain.port.saida.PautaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarPautasService implements ListarPautasUseCase {

    private final PautaRepositorio pautaRepositorio;

    @Override
    public List<Pauta> listar() {
        return pautaRepositorio.listarTodas();
    }
}
