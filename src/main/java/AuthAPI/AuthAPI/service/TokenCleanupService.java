package AuthAPI.AuthAPI.service;

import AuthAPI.AuthAPI.model.PasswordResetToken;
import AuthAPI.AuthAPI.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenCleanupService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Scheduled(cron = "0 0 2 * * *")
    public void limparTokensExpirados(){
        System.out.println(" [CRON JOB] Iniciando limpeza de tokens expirados no banco de dados...");
        passwordResetTokenRepository.deletarTokenExpirados(LocalDateTime.now());
        System.out.println(" [CRON JOB] Limpeza concluída com sucesso.");
    }
}
