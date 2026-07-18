package com.assembleia.votacao.adapter.entrada.web.tela;

public record ItemTexto(String tipo, String texto) implements ItemTela {

    public static ItemTexto de(String texto) {
        return new ItemTexto("TEXTO", texto);
    }
}
