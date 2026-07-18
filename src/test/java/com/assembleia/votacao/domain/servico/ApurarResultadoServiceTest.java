package com.assembleia.votacao.domain.servico;

import com.assembleia.votacao.domain.exception.PautaNaoEncontradaException;
import com.assembleia.votacao.domain.model.OpcaoVoto;
import com.assembleia.votacao.domain.model.Pauta;
import com.assembleia.votacao.domain.model.ResultadoApuracao;
import com.assembleia.votacao.domain.model.ResultadoFinal;
import com.assembleia.votacao.domain.port.saida.PautaRepositorio;
import com.assembleia.votacao.domain.port.saida.VotoRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApurarResultadoServiceTest {

    private static final Long PAUTA_ID = 1L;

    @Mock
    private VotoRepositorio votoRepositorio;

    @Mock
    private PautaRepositorio pautaRepositorio;

    @InjectMocks
    private ApurarResultadoService apurarResultadoService;

    @Test
    void deveApurarComoAprovadaQuandoSimMaiorQueNao() {
        when(pautaRepositorio.buscarPorId(PAUTA_ID))
                .thenReturn(Optional.of(new Pauta(PAUTA_ID, "Titulo", "Descricao", Instant.now())));
        when(votoRepositorio.contarPorPautaIdEOpcao(PAUTA_ID, OpcaoVoto.SIM)).thenReturn(3L);
        when(votoRepositorio.contarPorPautaIdEOpcao(PAUTA_ID, OpcaoVoto.NAO)).thenReturn(1L);

        ResultadoApuracao resultado = apurarResultadoService.apurar(PAUTA_ID);

        assertThat(resultado.totalSim()).isEqualTo(3L);
        assertThat(resultado.totalNao()).isEqualTo(1L);
        assertThat(resultado.resultado()).isEqualTo(ResultadoFinal.APROVADA);
    }

    @Test
    void deveApurarComoReprovadaQuandoNaoMaiorQueSim() {
        when(pautaRepositorio.buscarPorId(PAUTA_ID))
                .thenReturn(Optional.of(new Pauta(PAUTA_ID, "Titulo", "Descricao", Instant.now())));
        when(votoRepositorio.contarPorPautaIdEOpcao(PAUTA_ID, OpcaoVoto.SIM)).thenReturn(1L);
        when(votoRepositorio.contarPorPautaIdEOpcao(PAUTA_ID, OpcaoVoto.NAO)).thenReturn(3L);

        ResultadoApuracao resultado = apurarResultadoService.apurar(PAUTA_ID);

        assertThat(resultado.resultado()).isEqualTo(ResultadoFinal.REPROVADA);
    }

    @Test
    void deveApurarComoEmpateQuandoTotaisIguais() {
        when(pautaRepositorio.buscarPorId(PAUTA_ID))
                .thenReturn(Optional.of(new Pauta(PAUTA_ID, "Titulo", "Descricao", Instant.now())));
        when(votoRepositorio.contarPorPautaIdEOpcao(PAUTA_ID, OpcaoVoto.SIM)).thenReturn(2L);
        when(votoRepositorio.contarPorPautaIdEOpcao(PAUTA_ID, OpcaoVoto.NAO)).thenReturn(2L);

        ResultadoApuracao resultado = apurarResultadoService.apurar(PAUTA_ID);

        assertThat(resultado.resultado()).isEqualTo(ResultadoFinal.EMPATE);
    }

    @Test
    void deveLancarExcecaoQuandoPautaNaoExiste() {
        when(pautaRepositorio.buscarPorId(PAUTA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> apurarResultadoService.apurar(PAUTA_ID))
                .isInstanceOf(PautaNaoEncontradaException.class);
    }
}
