package com.assembleia.votacao.adapter.saida.integracao;

import com.assembleia.votacao.domain.exception.AssociadoNaoAptoException;
import com.assembleia.votacao.domain.exception.CpfInvalidoException;
import com.assembleia.votacao.domain.exception.ServicoVerificacaoIndisponivelException;
import com.assembleia.votacao.domain.port.saida.VerificadorElegibilidadeAssociado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class VerificadorElegibilidadeAssociadoAdapter implements VerificadorElegibilidadeAssociado {

    private static final String STATUS_APTO = "ABLE_TO_VOTE";

    private final RestClient verificacaoCpfRestClient;

    @Override
    public void verificar(String cpf) {
        RespostaVerificacaoCpf resposta;
        try {
            resposta = verificacaoCpfRestClient.get()
                    .uri("/users/{cpf}", cpf)
                    .retrieve()
                    .body(RespostaVerificacaoCpf.class);
        } catch (HttpClientErrorException.NotFound excecao) {
            throw new CpfInvalidoException(cpf);
        } catch (RestClientException excecao) {
            throw new ServicoVerificacaoIndisponivelException(cpf, excecao);
        }

        if (resposta == null || !STATUS_APTO.equals(resposta.status())) {
            throw new AssociadoNaoAptoException(cpf);
        }
    }
}
