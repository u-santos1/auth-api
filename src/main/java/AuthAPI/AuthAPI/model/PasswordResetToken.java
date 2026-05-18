package AuthAPI.AuthAPI.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor

public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "usuario_id")
    private Usuario usuario;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dateExpiracao;

    public boolean isExpirado(){
        return LocalDateTime.now().isAfter(this.dateExpiracao);
    }

    public PasswordResetToken(String token, Usuario usuario, LocalDateTime dateExpiracao) {
        this.dateExpiracao = dateExpiracao;
        this.usuario = usuario;
        this.token = token;
    }
}
