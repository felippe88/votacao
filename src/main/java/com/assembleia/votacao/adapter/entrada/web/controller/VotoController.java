package com.assembleia.votacao.adapter.entrada.web.controller;

import com.assembleia.votacao.adapter.entrada.web.dto.RegistrarVotoRequest;
import com.assembleia.votacao.adapter.entrada.web.tela.Tela;
import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.adapter.entrada.web.tela.fabrica.VotoTelaFabrica;
import com.assembleia.votacao.domain.model.Voto;
import com.assembleia.votacao.domain.port.entrada.RegistrarVotoUseCase;
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
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
@RequiredArgsConstructor
@Tag(name = "Votos", description = "Registro de votos dos associados em uma pauta")
public class VotoController {

    private final RegistrarVotoUseCase registrarVotoUseCase;
    private final VotoTelaFabrica votoTelaFabrica;

    @PostMapping
    @Operation(summary = "Registra o voto de um associado em uma pauta",
            description = "Aceita um voto Sim/Não por associado por pauta, apenas enquanto a sessão de "
                    + "votação estiver aberta. Um mesmo associado não pode votar duas vezes na mesma pauta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Voto registrado; tela FORMULARIO informativa",
                    content = @Content(schema = @Schema(implementation = TelaFormulario.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Associado já votou nesta pauta"),
            @ApiResponse(responseCode = "422", description = "Sessão de votação encerrada ou inexistente")
    })
    public ResponseEntity<Tela> votar(@PathVariable Long pautaId,
                                       @Valid @RequestBody RegistrarVotoRequest request) {
        Voto voto = registrarVotoUseCase.registrar(pautaId, request.associadoId(), request.opcao());
        return ResponseEntity.status(HttpStatus.CREATED).body(votoTelaFabrica.confirmacaoVoto(voto));
    }
}
