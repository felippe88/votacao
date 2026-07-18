package com.assembleia.votacao.domain.servico;

import com.assembleia.votacao.domain.model.Pauta;
import com.assembleia.votacao.domain.port.saida.PautaRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarPautaServiceTest {

    @Mock
    private PautaRepositorio pautaRepositorio;

    @InjectMocks
    private CadastrarPautaService cadastrarPautaService;

    @Test
    void deveCadastrarPautaEDelegarParaRepositorio() {
        when(pautaRepositorio.salvar(any(Pauta.class))).thenAnswer(invocacao -> {
            Pauta pauta = invocacao.getArgument(0);
            return new Pauta(1L, pauta.titulo(), pauta.descricao(), pauta.dataCriacao());
        });

        Pauta resultado = cadastrarPautaService.cadastrar("Reforma do estatuto", "Discussão da reforma");

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.titulo()).isEqualTo("Reforma do estatuto");
        assertThat(resultado.descricao()).isEqualTo("Discussão da reforma");
        assertThat(resultado.dataCriacao()).isNotNull();
        verify(pautaRepositorio).salvar(any(Pauta.class));
    }
}
