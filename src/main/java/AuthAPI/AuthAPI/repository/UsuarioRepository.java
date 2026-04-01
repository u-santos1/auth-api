package AuthAPI.AuthAPI.repository;

import AuthAPI.AuthAPI.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Boolean existsByEmail(String email);
}
