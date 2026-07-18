package com.assembleia.votacao.adapter.saida.persistencia.repositorio;

import com.assembleia.votacao.adapter.saida.persistencia.entidade.VotoEntidade;
import com.assembleia.votacao.domain.model.OpcaoVoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoJpaRepository extends JpaRepository<VotoEntidade, Long> {

    long countByPautaIdAndOpcao(Long pautaId, OpcaoVoto opcao);
}
