package AuthAPI.AuthAPI.infra.segurity.exception;

import AuthAPI.AuthAPI.infra.segurity.RegraDeNegocioException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class TratadorDeErros {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity error400(MethodArgumentNotValidException exception){
        var erros = exception.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity tratarErrorRegraDeNegocio(RegraDeNegocioException exception){
        return ResponseEntity.badRequest().body(new DadosErroSimples(exception.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarErro404(EntityNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosErroSimples(exception.getMessage()));
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity tratarErro401(BadCredentialsException exception){
        log.warn("Tentativa de acesso negada {} ", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DadosErroSimples("Email ou senha incorretos"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity tratarErro500(Exception exception){
        log.warn("Erro Interno inesperado", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DadosErroSimples("Erro Interno"));
    }
    private record DadosErroValidacao(String campo, String mensagem){
        public DadosErroValidacao(FieldError error){
            this(error.getField(), error.getDefaultMessage());
        }
    }
    private record DadosErroSimples(String mensagem){
    }
}
