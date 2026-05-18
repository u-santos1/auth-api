package AuthAPI.AuthAPI.infra.segurity;

import AuthAPI.AuthAPI.model.Usuario;
import AuthAPI.AuthAPI.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository){
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       try {

        var tokenJWT = recuperarToken(request);
        if (tokenJWT != null){
            var subject = tokenService.getSubject(tokenJWT);

            var usuario = usuarioRepository.findByEmail(subject)
                    .orElseThrow(()-> new UsernameNotFoundException("Usuario nao encontrado"));
            if (!usuario.isEnabled() || !usuario.isAccountNonLocked()) {
                throw new TokenException("Usuario desativado ou bloqueado");
            }
            var authentication = new UsernamePasswordAuthenticationToken(usuario,null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }}catch (Exception e){
           SecurityContextHolder.clearContext();
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           response.getWriter().write("Token invalido ou expirado");
           return;
       }
       filterChain.doFilter(request, response);
    }
    private String recuperarToken(HttpServletRequest request){
        var authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.toLowerCase().startsWith("bearer ")) {
            return authorizationHeader.substring(7).trim();

        }
        return null;
    }

}
