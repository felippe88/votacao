package com.assembleia.votacao.adapter.entrada.web.tela.fabrica;

import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.adapter.entrada.web.tela.TelaSelecao;
import com.assembleia.votacao.config.PropriedadesAplicacao;
import com.assembleia.votacao.domain.model.Pauta;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PautaTelaFabricaTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private PautaTelaFabrica pautaTelaFabrica;

    @BeforeEach
    void configurar() {
        PropriedadesAplicacao propriedades = new PropriedadesAplicacao(
                "http://localhost:8080", new PropriedadesAplicacao.Sessao(60),
                new PropriedadesAplicacao.IntegracaoCpf("https://user-info.herokuapp.com"));
        pautaTelaFabrica = new PautaTelaFabrica(propriedades);
    }

    @Test
    void listaPautasDeveGerarTelaSelecaoConformeContrato() {
        List<Pauta> pautas = List.of(new Pauta(1L, "Reforma do estatuto", "desc", Instant.now()));

        TelaSelecao tela = pautaTelaFabrica.listaPautas(pautas);
        JsonNode json = objectMapper.valueToTree(tela);

        assertThat(json.get("tipo").asText()).isEqualTo("SELECAO");
        assertThat(json.get("titulo").asText()).isEqualTo("Pautas");
        JsonNode item = json.get("itens").get(0);
        assertThat(item.get("texto").asText()).isEqualTo("Reforma do estatuto");
        assertThat(item.get("url").asText()).isEqualTo("http://localhost:8080/api/v1/pautas/1/sessoes");
        assertThat(item.has("body")).isFalse();
    }

    @Test
    void confirmacaoCadastroDeveGerarTelaFormularioComUmBotaoESemBotaoCancelarNoJson() {
        Pauta pauta = new Pauta(1L, "Reforma do estatuto", "desc", Instant.now());

        TelaFormulario tela = pautaTelaFabrica.confirmacaoCadastro(pauta);
        JsonNode json = objectMapper.valueToTree(tela);

        assertThat(json.get("tipo").asText()).isEqualTo("FORMULARIO");
        assertThat(json.get("botaoOk").get("texto").asText()).isEqualTo("Abrir sessão de votação");
        assertThat(json.get("botaoOk").get("url").asText())
                .isEqualTo("http://localhost:8080/api/v1/pautas/1/sessoes");
        assertThat(json.has("botaoCancelar")).isFalse();
    }
}
