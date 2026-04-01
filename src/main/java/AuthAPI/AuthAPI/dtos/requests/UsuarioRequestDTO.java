package AuthAPI.AuthAPI.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(

        @NotBlank(message = "Usuario nao pode esta vazio") String nome,
        @Email(message = "Formato invalito") String email,
        @NotBlank(message = "Senha na pode esta vazia")
        @Size(min= 6, message = "Senha deve ter no minimo 6 caracteres") String senha
) {
}
