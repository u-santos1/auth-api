package AuthAPI.AuthAPI.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetSenhaDTO(
        @NotBlank(message = "O token e obrigatorio")
        String token,

        @NotBlank
        @Size(min = 8, message = "Senha deve ter no minino 8 caracteres")
        String novaSenha
) {
}
