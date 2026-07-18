package com.assembleia.votacao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record PropriedadesAplicacao(String baseUrl, Sessao sessao) {

    public record Sessao(long duracaoPadraoSegundos) {
    }
}
