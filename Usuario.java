package pe.ollantay.hcweb.model;

import jakarta.persistence.*;

/**
 * Usuario del sistema. Rol: "ADMIN" o "USUARIO".
 */
@Entity
@Table(name = "usuario",
       uniqueConstraints = @UniqueConstraint(columnNames = "usuario"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String usuario;

    @Column(nullable = false)
    private String clave;   // guardada encriptada (BCrypt)

    @Column(nullable = false)
    private String rol;     // "ADMIN" o "USUARIO"

    public Usuario() {
    }

    public Usuario(String usuario, String clave, String rol) {
        this.usuario = usuario;
        this.clave = clave;
        this.rol = rol;
    }

    public boolean esAdmin() {
        return "ADMIN".equalsIgnoreCase(rol);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
