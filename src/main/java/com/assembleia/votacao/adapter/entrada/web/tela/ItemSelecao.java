package com.assembleia.votacao.adapter.entrada.web.tela;

import java.util.Map;

public record ItemSelecao(String texto, String url, Map<String, Object> body) {

    public static ItemSelecao de(String texto, String url, Map<String, Object> body) {
        return new ItemSelecao(texto, url, body);
    }

    public static ItemSelecao semBody(String texto, String url) {
        return new ItemSelecao(texto, url, null);
    }
}
