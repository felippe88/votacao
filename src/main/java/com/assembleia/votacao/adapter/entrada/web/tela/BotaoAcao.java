package com.assembleia.votacao.adapter.entrada.web.tela;

import java.util.Map;

public record BotaoAcao(String texto, String url, Map<String, Object> body) {

    public static BotaoAcao de(String texto, String url, Map<String, Object> body) {
        return new BotaoAcao(texto, url, body);
    }

    public static BotaoAcao semBody(String texto, String url) {
        return new BotaoAcao(texto, url, null);
    }
}
