package pe.ollantay.hcweb.service;

import org.springframework.stereotype.Service;
import pe.ollantay.hcweb.model.Paciente;
import pe.ollantay.hcweb.repo.PacienteRepository;

import java.util.*;

@Service
public class PacienteService {

    /** Distritos disponibles (edítalos según tu centro). */
    public static final List<String> DISTRITOS = List.of(
        "San Juan de Lurigancho", "Ate", "Comas", "Villa El Salvador", "San Martin de Porres",
        "Villa Maria del Triunfo", "Los Olivos", "Puente Piedra", "Chorrillos", "Carabayllo",
        "Santa Anita", "El Agustino", "Independencia", "La Victoria", "Rimac", "Surco",
        "San Miguel", "Brena", "Callao", "Lurin"
    );

    public static final List<String> TIPOS_DOC = List.of("DNI", "Carné de extranjería");

    private final PacienteRepository repo;

    public PacienteService(PacienteRepository repo) {
        this.repo = repo;
    }

    public List<Paciente> listarTodos() {
        return repo.findAllByOrderByNombres();
    }

    public List<Paciente> buscar(String texto) {
        if (texto == null || texto.isBlank()) return listarTodos();
        return repo.buscar(texto.trim());
    }

    public List<Paciente> porDistrito(String distrito) {
        if (distrito == null || distrito.isBlank()) return listarTodos();
        return repo.findByDistritoOrderByNombres(distrito);
    }

    public Paciente porId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Paciente guardar(Paciente p) {
        return repo.save(p);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    /** True si la HC ya existe en otro registro. idActual puede ser null (nuevo). */
    public boolean existeHc(String hc, Long idActual) {
        if (hc == null || hc.isBlank()) return false;
        return repo.findByHc(hc.trim())
                .map(p -> !p.getId().equals(idActual))
                .orElse(false);
    }

    /** True si el documento ya existe en otro registro. */
    public boolean existeDocumento(String numero, Long idActual) {
        if (numero == null || numero.isBlank()) return false;
        for (Paciente p : repo.findByNumeroDocumento(numero.trim())) {
            if (!p.getId().equals(idActual)) return true;
        }
        return false;
    }

    /** Pacientes con nombres parecidos (posibles familiares), excluyendo el actual. */
    public List<Paciente> coincidencias(String nombres, Long idActual) {
        if (nombres == null || nombres.trim().length() < 3) return List.of();
        Map<Long, Paciente> mapa = new LinkedHashMap<>();
        for (String palabra : nombres.trim().split("\\s+")) {
            if (palabra.length() < 2) continue;
            for (Paciente p : repo.findByNombresContainingIgnoreCaseOrderByNombres(palabra)) {
                if (idActual == null || !p.getId().equals(idActual)) {
                    mapa.put(p.getId(), p);
                }
            }
        }
        return new ArrayList<>(mapa.values());
    }
}
