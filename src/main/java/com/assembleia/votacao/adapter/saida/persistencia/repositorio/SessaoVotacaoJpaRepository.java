package com.assembleia.votacao.adapter.saida.persistencia.repositorio;

import com.assembleia.votacao.adapter.saida.persistencia.entidade.SessaoVotacaoEntidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessaoVotacaoJpaRepository extends JpaRepository<SessaoVotacaoEntidade, Long> {

    Optional<SessaoVotacaoEntidade> findByPautaId(Long pautaId);

    boolean existsByPautaId(Long pautaId);
}
