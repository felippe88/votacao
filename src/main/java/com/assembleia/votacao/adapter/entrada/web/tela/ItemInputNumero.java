package com.assembleia.votacao.adapter.entrada.web.tela;

public record ItemInputNumero(String tipo, String id, String titulo, Number valor) implements ItemTela {

    public static ItemInputNumero de(String id, String titulo, Number valor) {
        return new ItemInputNumero("INPUT_NUMERO", id, titulo, valor);
    }
}
