package com.assembleia.votacao.adapter.entrada.web.tela.fabrica;

import com.assembleia.votacao.adapter.entrada.web.tela.ItemTela;
import com.assembleia.votacao.adapter.entrada.web.tela.ItemTexto;
import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.domain.model.ResultadoApuracao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResultadoTelaFabrica {

    public TelaFormulario tela(ResultadoApuracao resultadoApuracao) {
        List<ItemTela> itens = List.of(
                ItemTexto.de("Pauta: " + resultadoApuracao.tituloPauta()),
                ItemTexto.de("Total de votos Sim: " + resultadoApuracao.totalSim()),
                ItemTexto.de("Total de votos Não: " + resultadoApuracao.totalNao()),
                ItemTexto.de("Resultado: " + resultadoApuracao.resultado()));
        return TelaFormulario.informativa("Resultado da votação", itens);
    }
}
