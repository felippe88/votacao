package com.assembleia.votacao.adapter.entrada.web.controller;

import com.assembleia.votacao.adapter.entrada.web.dto.CadastrarPautaRequest;
import com.assembleia.votacao.adapter.entrada.web.tela.Tela;
import com.assembleia.votacao.adapter.entrada.web.tela.fabrica.PautaTelaFabrica;
import com.assembleia.votacao.domain.model.Pauta;
import com.assembleia.votacao.domain.port.entrada.CadastrarPautaUseCase;
import com.assembleia.votacao.domain.port.entrada.ListarPautasUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "Pautas", description = "Cadastro e listagem de pautas de assembleia")
public class PautaController {

    private final CadastrarPautaUseCase cadastrarPautaUseCase;
    private final ListarPautasUseCase listarPautasUseCase;
    private final PautaTelaFabrica pautaTelaFabrica;

    @GetMapping
    @Operation(summary = "Lista as pautas cadastradas",
            description = "Retorna uma tela SELECAO com uma opção por pauta; cada opção aponta para o "
                    + "endpoint de abertura de sessão daquela pauta.")
    @ApiResponse(responseCode = "200", description = "Tela SELECAO com a lista de pautas")
    public Tela listar() {
        return pautaTelaFabrica.listaPautas(listarPautasUseCase.listar());
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova pauta",
            description = "Cria a pauta e retorna uma tela FORMULARIO de confirmação, com um botão que "
                    + "leva à abertura da sessão de votação.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pauta cadastrada; tela FORMULARIO de confirmação"),
            @ApiResponse(responseCode = "400", description = "Título ausente ou inválido")
    })
    public ResponseEntity<Tela> cadastrar(@Valid @RequestBody CadastrarPautaRequest request) {
        Pauta pauta = cadastrarPautaUseCase.cadastrar(request.titulo(), request.descricao());
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaTelaFabrica.confirmacaoCadastro(pauta));
    }
}
