package com.assembleia.votacao.adapter.entrada.web.tela.fabrica;

import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.domain.model.OpcaoVoto;
import com.assembleia.votacao.domain.model.Voto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class VotoTelaFabricaTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final VotoTelaFabrica votoTelaFabrica = new VotoTelaFabrica();

    @Test
    void confirmacaoVotoDeveGerarTelaInformativaSemBotoes() {
        Voto voto = new Voto(1L, 10L, "123", OpcaoVoto.SIM, Instant.now());

        TelaFormulario tela = votoTelaFabrica.confirmacaoVoto(voto);
        JsonNode json = objectMapper.valueToTree(tela);

        assertThat(json.get("tipo").asText()).isEqualTo("FORMULARIO");
        assertThat(json.get("itens").get(0).get("tipo").asText()).isEqualTo("TEXTO");
        assertThat(json.has("botaoOk")).isFalse();
        assertThat(json.has("botaoCancelar")).isFalse();
    }
}
