package com.assembleia.votacao.adapter.entrada.web.tela;

public record ItemInputData(String tipo, String id, String titulo, String valor) implements ItemTela {

    public static ItemInputData de(String id, String titulo, String valor) {
        return new ItemInputData("INPUT_DATA", id, titulo, valor);
    }
}
