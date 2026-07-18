package com.assembleia.votacao.domain.servico;

import com.assembleia.votacao.config.PropriedadesAplicacao;
import com.assembleia.votacao.domain.exception.PautaNaoEncontradaException;
import com.assembleia.votacao.domain.exception.SessaoJaAbertaException;
import com.assembleia.votacao.domain.model.Pauta;
import com.assembleia.votacao.domain.model.SessaoVotacao;
import com.assembleia.votacao.domain.port.saida.PautaRepositorio;
import com.assembleia.votacao.domain.port.saida.SessaoVotacaoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbrirSessaoVotacaoServiceTest {

    private static final Long PAUTA_ID = 1L;

    @Mock
    private SessaoVotacaoRepositorio sessaoVotacaoRepositorio;

    @Mock
    private PautaRepositorio pautaRepositorio;

    private AbrirSessaoVotacaoService service;

    @BeforeEach
    void configurar() {
        PropriedadesAplicacao propriedades = new PropriedadesAplicacao(
                "http://localhost:8080", new PropriedadesAplicacao.Sessao(60));
        service = new AbrirSessaoVotacaoService(sessaoVotacaoRepositorio, pautaRepositorio, propriedades);
    }

    @Test
    void deveAbrirSessaoComDuracaoPadraoQuandoNaoInformada() {
        when(pautaRepositorio.buscarPorId(PAUTA_ID))
                .thenReturn(Optional.of(new Pauta(PAUTA_ID, "Titulo", "Descricao", Instant.now())));
        when(sessaoVotacaoRepositorio.existePorPautaId(PAUTA_ID)).thenReturn(false);
        when(sessaoVotacaoRepositorio.salvar(any(SessaoVotacao.class))).thenAnswer(inv -> inv.getArgument(0));

        SessaoVotacao sessao = service.abrir(PAUTA_ID, null);

        assertThat(Duration.between(sessao.abertura(), sessao.fechamento()).getSeconds()).isEqualTo(60);
    }

    @Test
    void deveUsarDuracaoInformadaQuandoPresente() {
        when(pautaRepositorio.buscarPorId(PAUTA_ID))
                .thenReturn(Optional.of(new Pauta(PAUTA_ID, "Titulo", "Descricao", Instant.now())));
        when(sessaoVotacaoRepositorio.existePorPautaId(PAUTA_ID)).thenReturn(false);
        when(sessaoVotacaoRepositorio.salvar(any(SessaoVotacao.class))).thenAnswer(inv -> inv.getArgument(0));

        SessaoVotacao sessao = service.abrir(PAUTA_ID, 120L);

        assertThat(Duration.between(sessao.abertura(), sessao.fechamento()).getSeconds()).isEqualTo(120);
    }

    @Test
    void deveLancarExcecaoQuandoPautaNaoExiste() {
        when(pautaRepositorio.buscarPorId(PAUTA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.abrir(PAUTA_ID, null))
                .isInstanceOf(PautaNaoEncontradaException.class);
    }

    @Test
    void deveLancarExcecaoQuandoSessaoJaAberta() {
        when(pautaRepositorio.buscarPorId(PAUTA_ID))
                .thenReturn(Optional.of(new Pauta(PAUTA_ID, "Titulo", "Descricao", Instant.now())));
        when(sessaoVotacaoRepositorio.existePorPautaId(PAUTA_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.abrir(PAUTA_ID, null))
                .isInstanceOf(SessaoJaAbertaException.class);
    }
}
