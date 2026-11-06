package pe.ollantay.hcweb.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * Paciente / historia clinica (entidad de base de datos).
 */
@Entity
@Table(name = "paciente",
       uniqueConstraints = @UniqueConstraint(columnNames = "hc"))
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String hc;

    @Column(nullable = false)
    private String nombres;

    private LocalDate fechaNacimiento;
    private String tipoDocumento;    // "DNI" o "Carné de extranjería"
    private String numeroDocumento;
    private String telefono;
    private String direccion;
    private String distrito;

    /** Edad en años (calculada, no se guarda). -1 si no hay fecha. */
    public int getEdad() {
        if (fechaNacimiento == null) return -1;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /** Fecha como dd/MM/yyyy (o vacío). */
    public String getFechaTexto() {
        if (fechaNacimiento == null) return "";
        return fechaNacimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getHc() { return hc; }
    public void setHc(String hc) { this.hc = hc; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
}
