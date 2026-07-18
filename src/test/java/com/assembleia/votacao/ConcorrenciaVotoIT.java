package com.assembleia.votacao;

import com.assembleia.votacao.domain.exception.VotoDuplicadoException;
import com.assembleia.votacao.domain.model.OpcaoVoto;
import com.assembleia.votacao.domain.port.entrada.AbrirSessaoVotacaoUseCase;
import com.assembleia.votacao.domain.port.entrada.CadastrarPautaUseCase;
import com.assembleia.votacao.domain.port.entrada.RegistrarVotoUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class ConcorrenciaVotoIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private CadastrarPautaUseCase cadastrarPautaUseCase;

    @Autowired
    private AbrirSessaoVotacaoUseCase abrirSessaoVotacaoUseCase;

    @Autowired
    private RegistrarVotoUseCase registrarVotoUseCase;

    @Test
    void apenasUmVotoDeveSerAceitoQuandoMultiplasThreadsVotamComMesmoAssociado() throws Exception {
        Long pautaId = cadastrarPautaUseCase.cadastrar("Pauta concorrente", "descricao").id();
        abrirSessaoVotacaoUseCase.abrir(pautaId, 60L);

        int quantidadeThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(quantidadeThreads);
        CountDownLatch largada = new CountDownLatch(1);
        AtomicInteger sucessos = new AtomicInteger();
        AtomicInteger duplicados = new AtomicInteger();

        List<Future<?>> tarefas = new ArrayList<>();
        for (int i = 0; i < quantidadeThreads; i++) {
            tarefas.add(executor.submit(() -> {
                try {
                    largada.await();
                    registrarVotoUseCase.registrar(pautaId, "associado-concorrente", OpcaoVoto.SIM);
                    sucessos.incrementAndGet();
                } catch (VotoDuplicadoException excecao) {
                    duplicados.incrementAndGet();
                } catch (InterruptedException excecao) {
                    Thread.currentThread().interrupt();
                }
            }));
        }

        largada.countDown();
        for (Future<?> tarefa : tarefas) {
            tarefa.get(10, TimeUnit.SECONDS);
        }
        executor.shutdown();

        assertThat(sucessos.get()).isEqualTo(1);
        assertThat(duplicados.get()).isEqualTo(quantidadeThreads - 1);
    }
}
