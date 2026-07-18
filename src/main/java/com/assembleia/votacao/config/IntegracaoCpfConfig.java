package com.assembleia.votacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class IntegracaoCpfConfig {

    @Bean
    public RestClient verificacaoCpfRestClient(RestClient.Builder builder, PropriedadesAplicacao propriedades) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(5000);

        return builder
                .baseUrl(propriedades.integracaoCpf().baseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
