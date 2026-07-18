package com.assembleia.votacao.adapter.saida.persistencia.adaptador;

import com.assembleia.votacao.adapter.saida.persistencia.entidade.SessaoVotacaoEntidade;
import com.assembleia.votacao.adapter.saida.persistencia.repositorio.SessaoVotacaoJpaRepository;
import com.assembleia.votacao.domain.model.SessaoVotacao;
import com.assembleia.votacao.domain.port.saida.SessaoVotacaoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SessaoVotacaoRepositorioAdapter implements SessaoVotacaoRepositorio {

    private final SessaoVotacaoJpaRepository sessaoVotacaoJpaRepository;

    @Override
    public SessaoVotacao salvar(SessaoVotacao sessaoVotacao) {
        SessaoVotacaoEntidade entidade = new SessaoVotacaoEntidade(sessaoVotacao.id(), sessaoVotacao.pautaId(),
                sessaoVotacao.abertura(), sessaoVotacao.fechamento());
        return paraDominio(sessaoVotacaoJpaRepository.save(entidade));
    }

    @Override
    public Optional<SessaoVotacao> buscarPorPautaId(Long pautaId) {
        return sessaoVotacaoJpaRepository.findByPautaId(pautaId).map(this::paraDominio);
    }

    @Override
    public boolean existePorPautaId(Long pautaId) {
        return sessaoVotacaoJpaRepository.existsByPautaId(pautaId);
    }

    private SessaoVotacao paraDominio(SessaoVotacaoEntidade entidade) {
        return new SessaoVotacao(entidade.getId(), entidade.getPautaId(), entidade.getDataAbertura(),
                entidade.getDataFechamento());
    }
}
