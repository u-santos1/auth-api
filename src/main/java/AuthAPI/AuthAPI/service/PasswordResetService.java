package AuthAPI.AuthAPI.service;
import AuthAPI.AuthAPI.infra.segurity.RegraDeNegocioException;
import AuthAPI.AuthAPI.infra.segurity.SecurityLogger;
import AuthAPI.AuthAPI.model.PasswordResetToken;
import AuthAPI.AuthAPI.model.Usuario;
import AuthAPI.AuthAPI.repository.PasswordResetTokenRepository;
import AuthAPI.AuthAPI.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityLogger securityLogger;

    private String hashToken(String token){
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b: hash){
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            } return hexString.toString();
        }catch (Exception e){
            throw new RuntimeException("Erro ao processar token criptográfico", e);
        }
    }



    @Transactional
    public void solicitarResetDeSenha(String email, String ip) {
        securityLogger.logSolicitacaoRecuperacaoSenha(email,ip);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);


        if (usuarioOpt.isEmpty()){
            return;
        }
        Usuario usuario = usuarioOpt.get();
        String tokenPlano = UUID.randomUUID().toString();
        String tokenHash = hashToken(tokenPlano);

        LocalDateTime expiracao = LocalDateTime.now().plusMinutes(15);
        PasswordResetToken resetToken = new PasswordResetToken(tokenHash, usuario, expiracao);
        tokenRepository.save(resetToken);

        // Como estamos em lab local, vamos simular imprimindo no console:
        System.out.println("\n========================================================");
        System.out.println(" SIMULAÇÃO DE E-MAIL ENVIADO PARA: " + email);
        System.out.println(" Link de recuperação: http://localhost:8080/auth/reset-senha?token=" + tokenHash);
        System.out.println("========================================================\n");

    }

    @Transactional
    public void redefinirSenha(String tokenPlano, String novaSenha, String ip) {
        String tokenHash = hashToken(tokenPlano);
        PasswordResetToken resetToken = tokenRepository.findByToken(tokenHash)
                .orElseThrow(() -> new RegraDeNegocioException("Token inválido ou não encontrado."));

        if (resetToken.isExpirado()) {

            throw new RegraDeNegocioException("Este token expirou. Solicite a recuperação novamente.");
        }


        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));

        usuario.setDataUltimaAlteracaoSenha(Instant.now());
        usuarioRepository.save(usuario);
        securityLogger.logSenhaAlteradaComSucesso(usuario.getEmail(), ip);


        tokenRepository.delete(resetToken);
    }
}
