package pe.ollantay.hcweb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.ollantay.hcweb.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);
    long countByRol(String rol);
}
