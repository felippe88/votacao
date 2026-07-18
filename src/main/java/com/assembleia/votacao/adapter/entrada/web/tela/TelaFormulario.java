package com.assembleia.votacao.adapter.entrada.web.tela;

import java.util.List;

public record TelaFormulario(String tipo, String titulo, List<ItemTela> itens, BotaoAcao botaoOk,
                              BotaoAcao botaoCancelar) implements Tela {

    public static TelaFormulario comDoisBotoes(String titulo, List<ItemTela> itens, BotaoAcao botaoOk,
                                                BotaoAcao botaoCancelar) {
        return new TelaFormulario("FORMULARIO", titulo, itens, botaoOk, botaoCancelar);
    }

    public static TelaFormulario comUmBotao(String titulo, List<ItemTela> itens, BotaoAcao botaoOk) {
        return new TelaFormulario("FORMULARIO", titulo, itens, botaoOk, null);
    }

    public static TelaFormulario informativa(String titulo, List<ItemTela> itens) {
        return new TelaFormulario("FORMULARIO", titulo, itens, null, null);
    }
}
