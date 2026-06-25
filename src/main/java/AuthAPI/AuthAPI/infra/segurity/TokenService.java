package AuthAPI.AuthAPI.infra.segurity;

import AuthAPI.AuthAPI.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {


    @Value("${api.security.token.secret}")
    private String secret;

    @PostConstruct
    public void validarSecret(){
        if (secret == null || secret.isBlank() || secret.length() < 32){
            throw new IllegalStateException("FATAL: JWT_SECRET ausente ou muito curto. Mínimo de 32 caracteres exigido para segurança criptográfica.");
        }
    }

    public String gerarToken(Usuario usuario){
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("AuthAPI")
                    .withSubject(usuario.getEmail())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        }catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT");
        }
    }
    public String getSubject(String tokenJWT){
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            JWTVerifier verificaro = JWT.require(algoritmo)
                    .withIssuer("AuthAPI")
                    .build();
            DecodedJWT decodificador = verificaro.verify(tokenJWT);
            return decodificador.getSubject();
        } catch (JWTVerificationException e){
            throw new TokenException("Token invalido ou expirado");
        }
    }

    public Instant getIssuedAt(String tokenJWT){
        var algoritimo = Algorithm.HMAC256(secret);
        return JWT.require(algoritimo).withIssuer("AuthAPI").build()
                .verify(tokenJWT).getIssuedAtAsInstant();
    }
    public Instant dataExpiracao(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}

