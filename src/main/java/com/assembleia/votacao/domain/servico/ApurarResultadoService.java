package com.assembleia.votacao.domain.servico;

import com.assembleia.votacao.domain.exception.PautaNaoEncontradaException;
import com.assembleia.votacao.domain.model.OpcaoVoto;
import com.assembleia.votacao.domain.model.Pauta;
import com.assembleia.votacao.domain.model.ResultadoApuracao;
import com.assembleia.votacao.domain.model.ResultadoFinal;
import com.assembleia.votacao.domain.port.entrada.ApurarResultadoUseCase;
import com.assembleia.votacao.domain.port.saida.PautaRepositorio;
import com.assembleia.votacao.domain.port.saida.VotoRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApurarResultadoService implements ApurarResultadoUseCase {

    private final VotoRepositorio votoRepositorio;
    private final PautaRepositorio pautaRepositorio;

    @Override
    public ResultadoApuracao apurar(Long pautaId) {
        Pauta pauta = pautaRepositorio.buscarPorId(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));

        long totalSim = votoRepositorio.contarPorPautaIdEOpcao(pautaId, OpcaoVoto.SIM);
        long totalNao = votoRepositorio.contarPorPautaIdEOpcao(pautaId, OpcaoVoto.NAO);
        ResultadoFinal resultado = calcularResultado(totalSim, totalNao);

        log.info("Resultado apurado: pautaId={} totalSim={} totalNao={} resultado={}",
                pautaId, totalSim, totalNao, resultado);

        return new ResultadoApuracao(pautaId, pauta.titulo(), totalSim, totalNao, resultado);
    }

    private ResultadoFinal calcularResultado(long totalSim, long totalNao) {
        if (totalSim > totalNao) {
            return ResultadoFinal.APROVADA;
        }
        if (totalNao > totalSim) {
            return ResultadoFinal.REPROVADA;
        }
        return ResultadoFinal.EMPATE;
    }
}
