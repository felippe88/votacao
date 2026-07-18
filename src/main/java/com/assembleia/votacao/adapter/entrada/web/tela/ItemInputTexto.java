package com.assembleia.votacao.adapter.entrada.web.tela;

public record ItemInputTexto(String tipo, String id, String titulo, String valor) implements ItemTela {

    public static ItemInputTexto de(String id, String titulo, String valor) {
        return new ItemInputTexto("INPUT_TEXTO", id, titulo, valor);
    }
}
