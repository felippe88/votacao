package com.assembleia.votacao.adapter.saida.persistencia.adaptador;

import com.assembleia.votacao.adapter.saida.persistencia.entidade.PautaEntidade;
import com.assembleia.votacao.domain.exception.VotoDuplicadoException;
import com.assembleia.votacao.domain.model.OpcaoVoto;
import com.assembleia.votacao.domain.model.Voto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(VotoRepositorioAdapter.class)
class VotoRepositorioAdapterIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VotoRepositorioAdapter votoRepositorioAdapter;

    @Test
    void deveLancarVotoDuplicadoAoInserirMesmoAssociadoNaMesmaPauta() {
        Long pautaId = criarPauta();
        votoRepositorioAdapter.salvar(new Voto(null, pautaId, "123", OpcaoVoto.SIM, Instant.now()));

        Voto votoDuplicado = new Voto(null, pautaId, "123", OpcaoVoto.NAO, Instant.now());

        assertThatThrownBy(() -> votoRepositorioAdapter.salvar(votoDuplicado))
                .isInstanceOf(VotoDuplicadoException.class);
    }

    @Test
    void deveContarVotosPorPautaEOpcao() {
        Long pautaId = criarPauta();
        votoRepositorioAdapter.salvar(new Voto(null, pautaId, "1", OpcaoVoto.SIM, Instant.now()));
        votoRepositorioAdapter.salvar(new Voto(null, pautaId, "2", OpcaoVoto.SIM, Instant.now()));
        votoRepositorioAdapter.salvar(new Voto(null, pautaId, "3", OpcaoVoto.NAO, Instant.now()));

        assertThat(votoRepositorioAdapter.contarPorPautaIdEOpcao(pautaId, OpcaoVoto.SIM)).isEqualTo(2);
        assertThat(votoRepositorioAdapter.contarPorPautaIdEOpcao(pautaId, OpcaoVoto.NAO)).isEqualTo(1);
    }

    private Long criarPauta() {
        PautaEntidade pauta = new PautaEntidade(null, "Pauta de teste", "descricao", Instant.now());
        entityManager.persistAndFlush(pauta);
        return pauta.getId();
    }
}
