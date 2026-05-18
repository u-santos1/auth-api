package AuthAPI.AuthAPI.controller;
import AuthAPI.AuthAPI.dtos.TokenJwtDTO;
import AuthAPI.AuthAPI.dtos.requests.LoginDTO;
import AuthAPI.AuthAPI.infra.segurity.HttpUtils;
import AuthAPI.AuthAPI.infra.segurity.SecurityLogger;
import AuthAPI.AuthAPI.infra.segurity.TokenService;
import AuthAPI.AuthAPI.model.Usuario;


import AuthAPI.AuthAPI.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetService passwordResetService;
    private final SecurityLogger securityLogger;


    @PostMapping("/login")
    public ResponseEntity<TokenJwtDTO> efetuar(@RequestBody @Valid LoginDTO data, HttpServletRequest request){
        try {


        var autheticationToken = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var authetication = authenticationManager.authenticate(autheticationToken);

        Usuario usuarioAutenticado = (Usuario) authetication.getPrincipal();
        String tokenJWT = tokenService.gerarToken(usuarioAutenticado);
        return ResponseEntity.ok(new TokenJwtDTO(tokenJWT));}
        catch (org.springframework.security.authentication.BadCredentialsException ex){
            String ip = HttpUtils.getClientIp(request);
            securityLogger.logLoginFalho(data.email(), ip);

            throw ex;
        }
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> solicitarRecuperacao(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        String email = requestBody.get("email");
        String ip = HttpUtils.getClientIp(request);

        passwordResetService.solicitarResetDeSenha(email, ip);

        return ResponseEntity.ok("Se o e-mail estiver cadastrado, um link de recuperação foi enviado.");
    }

    @PostMapping("/reset-senha")
    public ResponseEntity<String> redefinirSenha(@RequestParam String token, @RequestBody @Valid LoginDTO requestDTO, HttpServletRequest request) {
        String ip = HttpUtils.getClientIp(request);
        passwordResetService.redefinirSenha(token, requestDTO.senha(), ip);
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
}
