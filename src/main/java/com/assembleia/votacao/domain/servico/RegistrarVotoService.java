package com.assembleia.votacao.domain.servico;

import com.assembleia.votacao.domain.exception.SessaoEncerradaException;
import com.assembleia.votacao.domain.model.OpcaoVoto;
import com.assembleia.votacao.domain.model.SessaoVotacao;
import com.assembleia.votacao.domain.model.Voto;
import com.assembleia.votacao.domain.port.entrada.RegistrarVotoUseCase;
import com.assembleia.votacao.domain.port.saida.SessaoVotacaoRepositorio;
import com.assembleia.votacao.domain.port.saida.VerificadorElegibilidadeAssociado;
import com.assembleia.votacao.domain.port.saida.VotoRepositorio;
import com.assembleia.votacao.domain.util.MascaraCpf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrarVotoService implements RegistrarVotoUseCase {

    private final VotoRepositorio votoRepositorio;
    private final SessaoVotacaoRepositorio sessaoVotacaoRepositorio;
    private final VerificadorElegibilidadeAssociado verificadorElegibilidadeAssociado;

    @Override
    public Voto registrar(Long pautaId, String associadoId, OpcaoVoto opcao) {
        SessaoVotacao sessao = sessaoVotacaoRepositorio.buscarPorPautaId(pautaId)
                .orElseThrow(() -> new SessaoEncerradaException(pautaId));

        if (!sessao.estaAberta(Instant.now())) {
            throw new SessaoEncerradaException(pautaId);
        }

        verificadorElegibilidadeAssociado.verificar(associadoId);

        Voto salvo = votoRepositorio.salvar(new Voto(null, pautaId, associadoId, opcao, Instant.now()));
        log.info("Voto aceito: pautaId={} associadoId={} opcao={}", pautaId, MascaraCpf.mascarar(associadoId), opcao);
        return salvo;
    }
}
