package com.assembleia.votacao.adapter.entrada.web.tela.fabrica;

import com.assembleia.votacao.adapter.entrada.web.tela.BotaoAcao;
import com.assembleia.votacao.adapter.entrada.web.tela.ItemSelecao;
import com.assembleia.votacao.adapter.entrada.web.tela.ItemTela;
import com.assembleia.votacao.adapter.entrada.web.tela.ItemTexto;
import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.adapter.entrada.web.tela.TelaSelecao;
import com.assembleia.votacao.config.PropriedadesAplicacao;
import com.assembleia.votacao.domain.model.Pauta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PautaTelaFabrica {

    private final PropriedadesAplicacao propriedadesAplicacao;

    public TelaSelecao listaPautas(List<Pauta> pautas) {
        List<ItemSelecao> itens = pautas.stream()
                .map(pauta -> ItemSelecao.semBody(pauta.titulo(), urlAbrirSessao(pauta.id())))
                .toList();
        return TelaSelecao.de("Pautas", itens);
    }

    public TelaFormulario confirmacaoCadastro(Pauta pauta) {
        List<ItemTela> itens = List.of(
                ItemTexto.de("Pauta \"" + pauta.titulo() + "\" cadastrada com sucesso."));
        BotaoAcao botaoOk = BotaoAcao.semBody("Abrir sessão de votação", urlAbrirSessao(pauta.id()));
        return TelaFormulario.comUmBotao("Pauta cadastrada", itens, botaoOk);
    }

    private String urlAbrirSessao(Long pautaId) {
        return propriedadesAplicacao.baseUrl() + "/api/v1/pautas/" + pautaId + "/sessoes";
    }
}
