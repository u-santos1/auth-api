package AuthAPI.AuthAPI.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetSenhaDTO(
        @NotBlank(message = "O token e obrigatorio")
        String token,

        @NotBlank(message = "Senha nao pode esta vazia")
        @Size(min=8, message="Mínimo 8 caracteres")
        @Pattern(regexp=".*[0-9].*", message="Deve conter ao menos um número")
        String novaSenha
) {
}
