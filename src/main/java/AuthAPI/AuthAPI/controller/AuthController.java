package AuthAPI.AuthAPI.controller;

import AuthAPI.AuthAPI.dtos.TokenJwtDTO;
import AuthAPI.AuthAPI.dtos.requests.LoginDTO;
import AuthAPI.AuthAPI.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenJwtDTO> efetuar(@RequestBody @Valid LoginDTO data){
        String tokenJWT = authService.efetuarLogin(data);
        return ResponseEntity.ok(new TokenJwtDTO(tokenJWT));
    }
}
