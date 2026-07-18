package com.assembleia.votacao.adapter.saida.persistencia.repositorio;

import com.assembleia.votacao.adapter.saida.persistencia.entidade.PautaEntidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PautaJpaRepository extends JpaRepository<PautaEntidade, Long> {
}
