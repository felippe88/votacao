package com.assembleia.votacao.adapter.entrada.web.tela.fabrica;

import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.domain.model.ResultadoApuracao;
import com.assembleia.votacao.domain.model.ResultadoFinal;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultadoTelaFabricaTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final ResultadoTelaFabrica resultadoTelaFabrica = new ResultadoTelaFabrica();

    @Test
    void telaDeveGerarTelaInformativaComTotais() {
        ResultadoApuracao resultadoApuracao = new ResultadoApuracao(1L, "Reforma do estatuto", 3L, 1L,
                ResultadoFinal.APROVADA);

        TelaFormulario tela = resultadoTelaFabrica.tela(resultadoApuracao);
        JsonNode json = objectMapper.valueToTree(tela);

        assertThat(json.get("tipo").asText()).isEqualTo("FORMULARIO");
        assertThat(json.get("itens")).hasSize(4);
        assertThat(json.has("botaoOk")).isFalse();
        assertThat(json.has("botaoCancelar")).isFalse();
    }
}
