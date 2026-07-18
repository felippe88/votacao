package com.assembleia.votacao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record PropriedadesAplicacao(String baseUrl, Sessao sessao, IntegracaoCpf integracaoCpf) {

    public PropriedadesAplicacao(String baseUrl, Sessao sessao) {
        this(baseUrl, sessao, new IntegracaoCpf("https://user-info.herokuapp.com"));
    }

    public record Sessao(long duracaoPadraoSegundos) {
    }

    public record IntegracaoCpf(String baseUrl) {
    }
}
