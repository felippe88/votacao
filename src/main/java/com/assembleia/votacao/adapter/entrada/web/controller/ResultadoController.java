package com.assembleia.votacao.adapter.entrada.web.controller;

import com.assembleia.votacao.adapter.entrada.web.tela.Tela;
import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.adapter.entrada.web.tela.fabrica.ResultadoTelaFabrica;
import com.assembleia.votacao.domain.port.entrada.ApurarResultadoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/resultado")
@RequiredArgsConstructor
@Tag(name = "Resultado", description = "Apuração do resultado da votação de uma pauta")
public class ResultadoController {

    private final ApurarResultadoUseCase apurarResultadoUseCase;
    private final ResultadoTelaFabrica resultadoTelaFabrica;

    @GetMapping
    @Operation(summary = "Apura o resultado da votação de uma pauta",
            description = "Conta os votos Sim/Não da pauta e retorna o resultado final (APROVADA, "
                    + "REPROVADA ou EMPATE) em uma tela informativa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tela FORMULARIO com os totais e o resultado",
                    content = @Content(schema = @Schema(implementation = TelaFormulario.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public Tela apurar(@PathVariable Long pautaId) {
        return resultadoTelaFabrica.tela(apurarResultadoUseCase.apurar(pautaId));
    }
}
