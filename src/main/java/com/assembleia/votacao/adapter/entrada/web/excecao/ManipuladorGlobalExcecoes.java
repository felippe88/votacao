package com.assembleia.votacao.adapter.entrada.web.excecao;

import com.assembleia.votacao.adapter.entrada.web.tela.ItemTexto;
import com.assembleia.votacao.adapter.entrada.web.tela.Tela;
import com.assembleia.votacao.adapter.entrada.web.tela.TelaFormulario;
import com.assembleia.votacao.domain.exception.AssociadoNaoAptoException;
import com.assembleia.votacao.domain.exception.CpfInvalidoException;
import com.assembleia.votacao.domain.exception.ExcecaoNegocio;
import com.assembleia.votacao.domain.exception.PautaNaoEncontradaException;
import com.assembleia.votacao.domain.exception.ServicoVerificacaoIndisponivelException;
import com.assembleia.votacao.domain.exception.SessaoEncerradaException;
import com.assembleia.votacao.domain.exception.SessaoJaAbertaException;
import com.assembleia.votacao.domain.exception.SessaoVotacaoNaoEncontradaException;
import com.assembleia.votacao.domain.exception.VotoDuplicadoException;
import tools.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ManipuladorGlobalExcecoes {

    @ExceptionHandler({PautaNaoEncontradaException.class, SessaoVotacaoNaoEncontradaException.class,
            CpfInvalidoException.class})
    public ResponseEntity<Tela> tratarNaoEncontrado(ExcecaoNegocio excecao) {
        log.warn(excecao.mensagemParaLog());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(telaErro(excecao.getMessage()));
    }

    @ExceptionHandler({SessaoJaAbertaException.class, VotoDuplicadoException.class})
    public ResponseEntity<Tela> tratarConflito(ExcecaoNegocio excecao) {
        log.warn(excecao.mensagemParaLog());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(telaErro(excecao.getMessage()));
    }

    @ExceptionHandler({SessaoEncerradaException.class, AssociadoNaoAptoException.class})
    public ResponseEntity<Tela> tratarRegraNegocioBloqueante(ExcecaoNegocio excecao) {
        log.warn(excecao.mensagemParaLog());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(telaErro(excecao.getMessage()));
    }

    @ExceptionHandler(ServicoVerificacaoIndisponivelException.class)
    public ResponseEntity<Tela> tratarServicoIndisponivel(ServicoVerificacaoIndisponivelException excecao) {
        log.error(excecao.mensagemParaLog(), excecao);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(telaErro(excecao.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Tela> tratarValidacao(MethodArgumentNotValidException excecao) {
        String mensagem = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validação falhou: {}", mensagem);
        return ResponseEntity.badRequest().body(telaErro(mensagem));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Tela> tratarCorpoInvalido(HttpMessageNotReadableException excecao) {
        String mensagem = mensagemCorpoInvalido(excecao);
        log.warn("Corpo da requisição inválido: {}", mensagem);
        return ResponseEntity.badRequest().body(telaErro(mensagem));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Tela> tratarErroGenerico(Exception excecao) {
        log.error("Erro inesperado", excecao);
        return ResponseEntity.internalServerError().body(telaErro("Erro interno inesperado."));
    }

    private String mensagemCorpoInvalido(HttpMessageNotReadableException excecao) {
        if (excecao.getCause() instanceof InvalidFormatException causa && causa.getTargetType().isEnum()) {
            String campo = causa.getPath().isEmpty() ? "campo"
                    : causa.getPath().get(causa.getPath().size() - 1).getPropertyName();
            String valoresAceitos = Arrays.stream(causa.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            return campo + ": valor \"" + causa.getValue() + "\" inválido. Valores aceitos: " + valoresAceitos;
        }
        return "Corpo da requisição inválido ou mal formatado";
    }

    private Tela telaErro(String mensagem) {
        return TelaFormulario.informativa("Erro", List.of(ItemTexto.de(mensagem)));
    }
}
