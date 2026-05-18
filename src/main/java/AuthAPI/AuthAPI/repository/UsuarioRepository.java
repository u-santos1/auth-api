package AuthAPI.AuthAPI.repository;

import AuthAPI.AuthAPI.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByEmailContaining(String dominio);
}
