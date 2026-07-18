package com.assembleia.votacao.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("API de Votação de Assembleias")
                .description("Cadastro de pautas, sessões de votação e apuração de resultado, "
                        + "com contrato de tela server-driven UI para o app mobile.")
                .version("v1"));
    }
}
