package com.assembleia.votacao.adapter.entrada.web.tela.fabrica;

import com.assembleia.votacao.adapter.entrada.web.tela.BotaoAcao;
import com.assembleia.votacao.adapter.entrada.web.tela.ItemInputTexto;
import com.assembleia.votacao.adapter.entrada.web.tela.ItemTela;
import com.assembleia.votacao.adapter.entrada.web.tela.ItemTexto;
import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.config.PropriedadesAplicacao;
import com.assembleia.votacao.domain.model.SessaoVotacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SessaoTelaFabrica {

    private final PropriedadesAplicacao propriedadesAplicacao;

    public TelaFormulario formularioVotacao(SessaoVotacao sessao) {
        String urlVoto = propriedadesAplicacao.baseUrl() + "/api/v1/pautas/" + sessao.pautaId() + "/votos";

        List<ItemTela> itens = List.of(
                ItemTexto.de("Sessão de votação aberta para a pauta " + sessao.pautaId()
                        + ". Encerra em " + sessao.fechamento() + "."),
                ItemInputTexto.de("associadoId", "Número do associado", ""));

        BotaoAcao votarSim = BotaoAcao.de("Votar Sim", urlVoto, Map.of("opcao", "SIM"));
        BotaoAcao votarNao = BotaoAcao.de("Votar Não", urlVoto, Map.of("opcao", "NAO"));

        return TelaFormulario.comDoisBotoes("Registrar voto", itens, votarSim, votarNao);
    }
}
