package AuthAPI.AuthAPI.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @NotBlank
        @Email String email,

        @NotBlank
        @Size(min = 8, message = "Senha deve ter no minino 8 caracteres")
        @Pattern(regexp = ".*[0-9].*", message = "Senha deve conter ao menos um numero")
        String senha
) {
}
