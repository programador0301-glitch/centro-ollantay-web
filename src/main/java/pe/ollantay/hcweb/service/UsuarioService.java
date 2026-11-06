package pe.ollantay.hcweb.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.ollantay.hcweb.model.Usuario;
import pe.ollantay.hcweb.repo.UsuarioRepository;

import java.util.List;

/**
 * Carga los usuarios para el login (Spring Security) y gestiona cuentas.
 */
@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = repo.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return User.withUsername(u.getUsuario())
                .password(u.getClave())
                .roles(u.getRol())   // ROLE_ADMIN o ROLE_USUARIO
                .build();
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public boolean existe(String usuario) {
        return repo.existsByUsuario(usuario);
    }

    public void crear(String usuario, String clave, String rol) {
        repo.save(new Usuario(usuario.trim(), encoder.encode(clave), rol));
    }

    public void cambiarClave(Long id, String nuevaClave) {
        repo.findById(id).ifPresent(u -> {
            u.setClave(encoder.encode(nuevaClave));
            repo.save(u);
        });
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public long contarAdmins() {
        return repo.countByRol("ADMIN");
    }

    public Usuario porId(Long id) {
        return repo.findById(id).orElse(null);
    }
}
