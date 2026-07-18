package com.assembleia.votacao.adapter.entrada.web.tela;

import java.util.List;

public record TelaSelecao(String tipo, String titulo, List<ItemSelecao> itens) implements Tela {

    public static TelaSelecao de(String titulo, List<ItemSelecao> itens) {
        return new TelaSelecao("SELECAO", titulo, itens);
    }
}
