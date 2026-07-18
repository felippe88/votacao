package com.assembleia.votacao.domain.servico;

import com.assembleia.votacao.domain.exception.SessaoEncerradaException;
import com.assembleia.votacao.domain.model.OpcaoVoto;
import com.assembleia.votacao.domain.model.SessaoVotacao;
import com.assembleia.votacao.domain.model.Voto;
import com.assembleia.votacao.domain.port.saida.SessaoVotacaoRepositorio;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarVotoServiceTest {

    private static final Long PAUTA_ID = 1L;
    private static final String ASSOCIADO_ID = "123";

    @Mock
    private VotoRepositorio votoRepositorio;

    @Mock
    private SessaoVotacaoRepositorio sessaoVotacaoRepositorio;

    @InjectMocks
    private RegistrarVotoService registrarVotoService;

    @Test
    void deveRegistrarVotoQuandoSessaoAberta() {
        Instant agora = Instant.now();
        SessaoVotacao sessao = new SessaoVotacao(1L, PAUTA_ID, agora.minusSeconds(10), agora.plusSeconds(50));
        when(sessaoVotacaoRepositorio.buscarPorPautaId(PAUTA_ID)).thenReturn(Optional.of(sessao));
        when(votoRepositorio.salvar(any(Voto.class))).thenAnswer(inv -> inv.getArgument(0));

        Voto voto = registrarVotoService.registrar(PAUTA_ID, ASSOCIADO_ID, OpcaoVoto.SIM);

        assertThat(voto.pautaId()).isEqualTo(PAUTA_ID);
        assertThat(voto.associadoId()).isEqualTo(ASSOCIADO_ID);
        assertThat(voto.opcao()).isEqualTo(OpcaoVoto.SIM);
        verify(votoRepositorio).salvar(any(Voto.class));
    }

    @Test
    void deveLancarExcecaoQuandoSessaoNaoExiste() {
        when(sessaoVotacaoRepositorio.buscarPorPautaId(PAUTA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrarVotoService.registrar(PAUTA_ID, ASSOCIADO_ID, OpcaoVoto.SIM))
                .isInstanceOf(SessaoEncerradaException.class);
        verify(votoRepositorio, never()).salvar(any(Voto.class));
    }

    @Test
    void deveLancarExcecaoQuandoSessaoJaFechou() {
        Instant agora = Instant.now();
        SessaoVotacao sessao = new SessaoVotacao(1L, PAUTA_ID, agora.minusSeconds(120), agora.minusSeconds(60));
        when(sessaoVotacaoRepositorio.buscarPorPautaId(PAUTA_ID)).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> registrarVotoService.registrar(PAUTA_ID, ASSOCIADO_ID, OpcaoVoto.SIM))
                .isInstanceOf(SessaoEncerradaException.class);
        verify(votoRepositorio, never()).salvar(any(Voto.class));
    }
}
