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



    @Transactional
    public void solicitarResetDeSenha(String email, String ip) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);


        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            String token = UUID.randomUUID().toString();
            securityLogger.logSolicitacaoRecuperacaoSenha(email, ip);

            LocalDateTime expiracao = LocalDateTime.now().plusMinutes(15);

            PasswordResetToken resetToken = new PasswordResetToken(token, usuario, expiracao);

            tokenRepository.save(resetToken);

            // TODO: Aqui entraria o envio real de e-mail (ex: JavaMailSender)
            // Como estamos em lab local, vamos simular imprimindo no console:
            System.out.println("\n========================================================");
            System.out.println(" SIMULAÇÃO DE E-MAIL ENVIADO PARA: " + email);
            System.out.println(" Link de recuperação: http://localhost:8080/auth/reset-senha?token=" + token);
            System.out.println("========================================================\n");
        }
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha, String ip) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RegraDeNegocioException("Token inválido ou não encontrado."));

        if (resetToken.isExpirado()) {

            throw new RegraDeNegocioException("Este token expirou. Solicite a recuperação novamente.");
        }


        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        securityLogger.logSenhaAlteradaComSucesso(usuario.getEmail(), ip);


        tokenRepository.delete(resetToken);
    }
}
