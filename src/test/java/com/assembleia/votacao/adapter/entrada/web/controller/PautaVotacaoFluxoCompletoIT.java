package com.assembleia.votacao.adapter.entrada.web.controller;

import com.assembleia.votacao.domain.port.saida.VerificadorElegibilidadeAssociado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class PautaVotacaoFluxoCompletoIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class VerificacaoElegibilidadeStubConfig {

        @Bean
        @Primary
        VerificadorElegibilidadeAssociado verificadorElegibilidadeAssociado() {
            return cpf -> { };
        }
    }

    @Test
    void deveExecutarFluxoCompletoDeCadastroAberturaVotoEApuracao() throws Exception {
        String respostaCadastro = mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Reforma do estatuto\",\"descricao\":\"Discussão da reforma\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("FORMULARIO"))
                .andReturn().getResponse().getContentAsString();

        long pautaId = extrairPautaId(respostaCadastro);

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/sessoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("FORMULARIO"))
                .andExpect(jsonPath("$.botaoOk.body.opcao").value("SIM"))
                .andExpect(jsonPath("$.botaoCancelar.body.opcao").value("NAO"));

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\":\"3\",\"opcao\":\"TALVEZ\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.itens[0].texto").value(
                        "opcao: valor \"TALVEZ\" inválido. Valores aceitos: SIM, NAO"));

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\":\"1\",\"opcao\":\"SIM\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\":\"2\",\"opcao\":\"NAO\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\":\"1\",\"opcao\":\"SIM\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/v1/pautas/" + pautaId + "/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[1].texto").value("Total de votos Sim: 1"))
                .andExpect(jsonPath("$.itens[2].texto").value("Total de votos Não: 1"))
                .andExpect(jsonPath("$.itens[3].texto").value("Resultado: EMPATE"));
    }

    private long extrairPautaId(String jsonCadastro) {
        Matcher matcher = Pattern.compile("/pautas/(\\d+)/sessoes").matcher(jsonCadastro);
        matcher.find();
        return Long.parseLong(matcher.group(1));
    }
}
