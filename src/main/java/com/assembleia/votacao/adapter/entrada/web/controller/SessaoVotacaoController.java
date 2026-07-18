package com.assembleia.votacao.adapter.entrada.web.controller;

import com.assembleia.votacao.adapter.entrada.web.dto.AbrirSessaoRequest;
import com.assembleia.votacao.adapter.entrada.web.tela.Tela;
import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.adapter.entrada.web.tela.fabrica.SessaoTelaFabrica;
import com.assembleia.votacao.domain.model.SessaoVotacao;
import com.assembleia.votacao.domain.port.entrada.AbrirSessaoVotacaoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/sessoes")
@RequiredArgsConstructor
@Tag(name = "Sessões de Votação", description = "Abertura da janela de votação de uma pauta")
public class SessaoVotacaoController {

    private final AbrirSessaoVotacaoUseCase abrirSessaoVotacaoUseCase;
    private final SessaoTelaFabrica sessaoTelaFabrica;

    @PostMapping
    @Operation(summary = "Abre a sessão de votação de uma pauta",
            description = "Abre a janela de votação pelo tempo informado em segundos (60s por padrão quando "
                    + "omitido) e retorna o formulário de voto, com os botões Votar Sim / Votar Não.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sessão aberta; tela FORMULARIO de votação",
                    content = @Content(schema = @Schema(implementation = TelaFormulario.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Já existe uma sessão de votação para esta pauta")
    })
    public ResponseEntity<Tela> abrir(@PathVariable Long pautaId,
                                       @Valid @RequestBody(required = false) AbrirSessaoRequest request) {
        Long duracaoSegundos = request != null ? request.duracaoSegundos() : null;
        SessaoVotacao sessao = abrirSessaoVotacaoUseCase.abrir(pautaId, duracaoSegundos);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoTelaFabrica.formularioVotacao(sessao));
    }
}
