package com.assembleia.votacao.domain.servico;

import com.assembleia.votacao.domain.model.Pauta;
import com.assembleia.votacao.domain.port.entrada.CadastrarPautaUseCase;
import com.assembleia.votacao.domain.port.saida.PautaRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CadastrarPautaService implements CadastrarPautaUseCase {

    private final PautaRepositorio pautaRepositorio;

    @Override
    public Pauta cadastrar(String titulo, String descricao) {
        Pauta pauta = new Pauta(null, titulo, descricao, Instant.now());
        Pauta salva = pautaRepositorio.salvar(pauta);
        log.info("Pauta cadastrada: id={} titulo={}", salva.id(), salva.titulo());
        return salva;
    }
}
