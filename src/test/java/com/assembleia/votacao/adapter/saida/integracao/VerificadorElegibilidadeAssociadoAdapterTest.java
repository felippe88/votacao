package com.assembleia.votacao.adapter.saida.integracao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.assembleia.votacao.domain.exception.AssociadoNaoAptoException;
import com.assembleia.votacao.domain.exception.CpfInvalidoException;
import com.assembleia.votacao.domain.exception.ServicoVerificacaoIndisponivelException;

class VerificadorElegibilidadeAssociadoAdapterTest {

    private static final String BASE_URL = "https://user-info.herokuapp.com";

    private MockRestServiceServer servidorMock;
    private VerificadorElegibilidadeAssociadoAdapter adapter;

    @BeforeEach
    void configurar() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        servidorMock = MockRestServiceServer.bindTo(builder).build();
        adapter = new VerificadorElegibilidadeAssociadoAdapter(builder.build());
    }

    @Test
    void naoDeveLancarExcecaoQuandoAssociadoApto() {
        servidorMock.expect(requestTo(BASE_URL + "/users/11111111111"))
                .andRespond(withSuccess("{\"status\":\"ABLE_TO_VOTE\"}", MediaType.APPLICATION_JSON));

        assertThatCode(() -> adapter.verificar("11111111111")).doesNotThrowAnyException();
    }

    @Test
    void deveLancarExcecaoQuandoAssociadoNaoApto() {
        servidorMock.expect(requestTo(BASE_URL + "/users/22222222222"))
                .andRespond(withSuccess("{\"status\":\"UNABLE_TO_VOTE\"}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> adapter.verificar("22222222222"))
                .isInstanceOf(AssociadoNaoAptoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoCpfInvalido() {
        servidorMock.expect(requestTo(BASE_URL + "/users/00000000000"))
                .andRespond(withStatus(NOT_FOUND));

        assertThatThrownBy(() -> adapter.verificar("00000000000"))
                .isInstanceOf(CpfInvalidoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoServicoIndisponivel() {
        servidorMock.expect(requestTo(BASE_URL + "/users/33333333333"))
                .andRespond(withStatus(INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> adapter.verificar("33333333333"))
                .isInstanceOf(ServicoVerificacaoIndisponivelException.class);
    }
}
