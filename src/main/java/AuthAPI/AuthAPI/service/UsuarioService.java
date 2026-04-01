package AuthAPI.AuthAPI.service;

import AuthAPI.AuthAPI.PerfilAcesso;
import AuthAPI.AuthAPI.dtos.UsuarioResponseDTO;
import AuthAPI.AuthAPI.dtos.requests.UsuarioRequestDTO;
import AuthAPI.AuthAPI.model.Usuario;
import AuthAPI.AuthAPI.repository.UsuarioRepository;
import org.springframework.boot.tomcat.autoconfigure.TomcatServerProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO criarAdmin(UsuarioRequestDTO data){
        Usuario salvo = salvarUsuarioNoBanco(data, PerfilAcesso.ADMIN);
        return new UsuarioResponseDTO(salvo);
    }
    public UsuarioResponseDTO criarUser(UsuarioRequestDTO data){
        Usuario salvo = salvarUsuarioNoBanco(data, PerfilAcesso.USER);
        return new UsuarioResponseDTO(salvo);
    }
    public Usuario salvarUsuarioNoBanco(UsuarioRequestDTO data, PerfilAcesso perfil){
        if (usuarioRepository.existsByEmail(data.email())){
            throw new RuntimeException("email ja em uso");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(data.nome().trim());
        usuario.setEmail(data.email().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(data.senha()));

        usuario.setPerfil(perfil);
        usuario.setAtivo(true);

        return usuarioRepository.save(usuario);
    }
}
