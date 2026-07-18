package com.assembleia.votacao.domain.servico;

import com.assembleia.votacao.config.PropriedadesAplicacao;
import com.assembleia.votacao.domain.exception.PautaNaoEncontradaException;
import com.assembleia.votacao.domain.exception.SessaoJaAbertaException;
import com.assembleia.votacao.domain.model.SessaoVotacao;
import com.assembleia.votacao.domain.port.entrada.AbrirSessaoVotacaoUseCase;
import com.assembleia.votacao.domain.port.saida.PautaRepositorio;
import com.assembleia.votacao.domain.port.saida.SessaoVotacaoRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AbrirSessaoVotacaoService implements AbrirSessaoVotacaoUseCase {

    private final SessaoVotacaoRepositorio sessaoVotacaoRepositorio;
    private final PautaRepositorio pautaRepositorio;
    private final PropriedadesAplicacao propriedadesAplicacao;

    @Override
    public SessaoVotacao abrir(Long pautaId, Long duracaoSegundos) {
        pautaRepositorio.buscarPorId(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));

        if (sessaoVotacaoRepositorio.existePorPautaId(pautaId)) {
            throw new SessaoJaAbertaException(pautaId);
        }

        long duracao = duracaoSegundos != null
                ? duracaoSegundos
                : propriedadesAplicacao.sessao().duracaoPadraoSegundos();

        Instant abertura = Instant.now();
        Instant fechamento = abertura.plusSeconds(duracao);

        SessaoVotacao salva = sessaoVotacaoRepositorio.salvar(
                new SessaoVotacao(null, pautaId, abertura, fechamento));

        log.info("Sessão de votação aberta: pautaId={} abertura={} fechamento={}",
                pautaId, abertura, fechamento);
        return salva;
    }
}
