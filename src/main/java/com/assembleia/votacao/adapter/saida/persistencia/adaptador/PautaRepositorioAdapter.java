package com.assembleia.votacao.adapter.saida.persistencia.adaptador;

import com.assembleia.votacao.adapter.saida.persistencia.entidade.PautaEntidade;
import com.assembleia.votacao.adapter.saida.persistencia.repositorio.PautaJpaRepository;
import com.assembleia.votacao.domain.model.Pauta;
import com.assembleia.votacao.domain.port.saida.PautaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PautaRepositorioAdapter implements PautaRepositorio {

    private final PautaJpaRepository pautaJpaRepository;

    @Override
    public Pauta salvar(Pauta pauta) {
        PautaEntidade entidade = new PautaEntidade(pauta.id(), pauta.titulo(), pauta.descricao(), pauta.dataCriacao());
        return paraDominio(pautaJpaRepository.save(entidade));
    }

    @Override
    public Optional<Pauta> buscarPorId(Long id) {
        return pautaJpaRepository.findById(id).map(this::paraDominio);
    }

    @Override
    public List<Pauta> listarTodas() {
        return pautaJpaRepository.findAll().stream().map(this::paraDominio).toList();
    }

    private Pauta paraDominio(PautaEntidade entidade) {
        return new Pauta(entidade.getId(), entidade.getTitulo(), entidade.getDescricao(), entidade.getDataCriacao());
    }
}
