package AuthAPI.AuthAPI.dtos;

import AuthAPI.AuthAPI.model.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email
) {
    public UsuarioResponseDTO(Usuario usuario){
        this(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
