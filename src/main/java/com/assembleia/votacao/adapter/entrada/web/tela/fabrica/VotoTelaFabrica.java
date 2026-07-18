package com.assembleia.votacao.adapter.entrada.web.tela.fabrica;

import com.assembleia.votacao.adapter.entrada.web.tela.ItemTela;
import com.assembleia.votacao.adapter.entrada.web.tela.ItemTexto;
import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.domain.model.Voto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VotoTelaFabrica {

    public TelaFormulario confirmacaoVoto(Voto voto) {
        List<ItemTela> itens = List.of(
                ItemTexto.de("Voto \"" + voto.opcao() + "\" registrado com sucesso para o associado "
                        + voto.associadoId() + "."));
        return TelaFormulario.informativa("Voto registrado", itens);
    }
}
