package pe.ollantay.hcweb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.ollantay.hcweb.model.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByHc(String hc);

    List<Paciente> findByNumeroDocumento(String numeroDocumento);

    List<Paciente> findByDistritoOrderByNombres(String distrito);

    List<Paciente> findAllByOrderByNombres();

    // Busqueda general por varias columnas
    @Query("""
           SELECT p FROM Paciente p
           WHERE lower(p.hc)              LIKE lower(concat('%', :t, '%'))
              OR lower(p.nombres)         LIKE lower(concat('%', :t, '%'))
              OR lower(p.numeroDocumento) LIKE lower(concat('%', :t, '%'))
              OR lower(p.telefono)        LIKE lower(concat('%', :t, '%'))
              OR lower(p.direccion)       LIKE lower(concat('%', :t, '%'))
              OR lower(p.distrito)        LIKE lower(concat('%', :t, '%'))
           ORDER BY p.nombres
           """)
    List<Paciente> buscar(@Param("t") String texto);

    // Coincidencia por una palabra del nombre (para posibles familiares)
    List<Paciente> findByNombresContainingIgnoreCaseOrderByNombres(String parte);

    long countByDistrito(String distrito);
}
