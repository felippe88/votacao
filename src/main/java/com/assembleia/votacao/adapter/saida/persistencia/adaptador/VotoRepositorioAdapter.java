package com.assembleia.votacao.adapter.saida.persistencia.adaptador;

import com.assembleia.votacao.adapter.saida.persistencia.entidade.VotoEntidade;
import com.assembleia.votacao.adapter.saida.persistencia.repositorio.VotoJpaRepository;
import com.assembleia.votacao.domain.exception.VotoDuplicadoException;
import com.assembleia.votacao.domain.model.OpcaoVoto;
import com.assembleia.votacao.domain.model.Voto;
import com.assembleia.votacao.domain.port.saida.VotoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VotoRepositorioAdapter implements VotoRepositorio {

    private final VotoJpaRepository votoJpaRepository;

    @Override
    public Voto salvar(Voto voto) {
        VotoEntidade entidade = new VotoEntidade(voto.id(), voto.pautaId(), voto.associadoId(), voto.opcao(),
                voto.dataVoto());
        try {
            return paraDominio(votoJpaRepository.saveAndFlush(entidade));
        } catch (DataIntegrityViolationException excecao) {
            throw new VotoDuplicadoException(voto.pautaId(), voto.associadoId());
        }
    }

    @Override
    public long contarPorPautaIdEOpcao(Long pautaId, OpcaoVoto opcao) {
        return votoJpaRepository.countByPautaIdAndOpcao(pautaId, opcao);
    }

    private Voto paraDominio(VotoEntidade entidade) {
        return new Voto(entidade.getId(), entidade.getPautaId(), entidade.getAssociadoId(), entidade.getOpcao(),
                entidade.getDataVoto());
    }
}
