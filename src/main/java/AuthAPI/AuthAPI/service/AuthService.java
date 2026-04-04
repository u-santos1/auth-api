package AuthAPI.AuthAPI.service;

import AuthAPI.AuthAPI.dtos.requests.LoginDTO;
import AuthAPI.AuthAPI.infra.segurity.TokenService;
import AuthAPI.AuthAPI.model.Usuario;
import AuthAPI.AuthAPI.repository.UsuarioRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpStrictTransportSecurityDsl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public AuthService(@Lazy AuthenticationManager authenticationManager, TokenService tokenService, UsuarioRepository usuarioRepository){
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + username));
    }

    public String efetuarLogin(LoginDTO data){
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);

        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        if (!usuarioAutenticado.isEnabled()){
            throw new DisabledException("Conta inativa");
        }
        return tokenService.gerarToken(usuarioAutenticado);
    }
}
