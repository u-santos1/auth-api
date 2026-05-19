package AuthAPI.AuthAPI.repository;

import AuthAPI.AuthAPI.model.PasswordResetToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    @EntityGraph(attributePaths = "usuario")
    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query(
            "DELETE FROM PasswordResetToken t WHERE t.dateExpiracao < :agora"
    )
    void deletarTokenExpirados(LocalDateTime agora);
}
