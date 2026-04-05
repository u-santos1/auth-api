package AuthAPI.AuthAPI.controller;
import AuthAPI.AuthAPI.dtos.TokenJwtDTO;
import AuthAPI.AuthAPI.dtos.requests.LoginDTO;
import AuthAPI.AuthAPI.infra.segurity.TokenService;
import AuthAPI.AuthAPI.model.Usuario;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    public AuthController(TokenService tokenService, AuthenticationManager authenticationManager){
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenJwtDTO> efetuar(@RequestBody @Valid LoginDTO data){
        var autheticationToken = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var authetication = authenticationManager.authenticate(autheticationToken);

        Usuario usuarioAutenticado = (Usuario) authetication.getPrincipal();
        String tokenJWT = tokenService.gerarToken(usuarioAutenticado);
        return ResponseEntity.ok(new TokenJwtDTO(tokenJWT));
    }
}
