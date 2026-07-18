package com.assembleia.votacao.adapter.entrada.web.tela.fabrica;

import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.config.PropriedadesAplicacao;
import com.assembleia.votacao.domain.model.SessaoVotacao;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SessaoTelaFabricaTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private SessaoTelaFabrica sessaoTelaFabrica;

    @BeforeEach
    void configurar() {
        PropriedadesAplicacao propriedades = new PropriedadesAplicacao(
                "http://localhost:8080", new PropriedadesAplicacao.Sessao(60),
                new PropriedadesAplicacao.IntegracaoCpf("https://user-info.herokuapp.com"));
        sessaoTelaFabrica = new SessaoTelaFabrica(propriedades);
    }

    @Test
    void formularioVotacaoDeveGerarTelaComDoisBotoesEInputTexto() {
        SessaoVotacao sessao = new SessaoVotacao(1L, 10L, Instant.now(), Instant.now().plusSeconds(60));

        TelaFormulario tela = sessaoTelaFabrica.formularioVotacao(sessao);
        JsonNode json = objectMapper.valueToTree(tela);

        assertThat(json.get("tipo").asText()).isEqualTo("FORMULARIO");
        JsonNode itemInput = json.get("itens").get(1);
        assertThat(itemInput.get("tipo").asText()).isEqualTo("INPUT_TEXTO");
        assertThat(itemInput.get("id").asText()).isEqualTo("associadoId");

        String urlEsperada = "http://localhost:8080/api/v1/pautas/10/votos";
        assertThat(json.get("botaoOk").get("url").asText()).isEqualTo(urlEsperada);
        assertThat(json.get("botaoOk").get("body").get("opcao").asText()).isEqualTo("SIM");
        assertThat(json.get("botaoCancelar").get("url").asText()).isEqualTo(urlEsperada);
        assertThat(json.get("botaoCancelar").get("body").get("opcao").asText()).isEqualTo("NAO");
    }
}
