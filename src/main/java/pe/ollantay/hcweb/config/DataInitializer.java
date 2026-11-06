package pe.ollantay.hcweb.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pe.ollantay.hcweb.model.Paciente;
import pe.ollantay.hcweb.repo.PacienteRepository;
import pe.ollantay.hcweb.service.PacienteService;
import pe.ollantay.hcweb.service.UsuarioService;
import pe.ollantay.hcweb.repo.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Al arrancar por primera vez (base vacía) crea:
 *  - Usuarios: admin/admin123 (ADMIN) y usuario/usuario123 (USUARIO)
 *  - Unos pacientes ficticios de ejemplo para ver el sistema funcionando.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepo;
    private final UsuarioService usuarioService;
    private final PacienteRepository pacienteRepo;

    public DataInitializer(UsuarioRepository usuarioRepo, UsuarioService usuarioService,
                           PacienteRepository pacienteRepo) {
        this.usuarioRepo = usuarioRepo;
        this.usuarioService = usuarioService;
        this.pacienteRepo = pacienteRepo;
    }

    @Override
    public void run(String... args) {
        // Usuarios por defecto
        if (usuarioRepo.count() == 0) {
            usuarioService.crear("admin", "admin123", "ADMIN");
            usuarioService.crear("usuario", "usuario123", "USUARIO");
        }

        // Pacientes de ejemplo (solo si no hay ninguno)
        if (pacienteRepo.count() == 0) {
            sembrarPacientes();
        }
    }

    private void sembrarPacientes() {
        String[] nombresH = {"Jose", "Juan", "Carlos", "Luis", "Miguel", "Jorge", "Pedro", "Cesar",
                "Daniel", "Diego", "Fernando", "Ricardo", "Andres", "Renzo", "Bruno", "Angel"};
        String[] nombresM = {"Maria", "Rosa", "Ana", "Carmen", "Luisa", "Juana", "Patricia", "Elena",
                "Sofia", "Lucia", "Carla", "Gabriela", "Milagros", "Fiorella", "Valeria", "Camila"};
        String[] apellidos = {"Quispe", "Mamani", "Flores", "Huaman", "Condori", "Vargas", "Rojas",
                "Torres", "Ramos", "Chavez", "Gutierrez", "Sanchez", "Ramirez", "Castillo", "Mendoza",
                "Vega", "Cruz", "Paredes", "Salazar", "Espinoza", "Palomino", "Huanca", "Villanueva"};
        String[] calles = {"Jr. Los Pinos", "Av. Las Flores", "Calle Lima", "Jr. Union", "Av. Peru",
                "Jr. Grau", "Av. Tupac Amaru", "Calle Santa Rosa", "Av. Universitaria", "Jr. Arequipa"};
        List<String> distritos = PacienteService.DISTRITOS;

        Random r = new Random(2026);
        LocalDate hoy = LocalDate.now();

        int total = 60;
        for (int i = 1; i <= total; i++) {
            Paciente p = new Paciente();
            p.setHc(String.valueOf(i));

            // 25% de las veces, mismos apellidos que el anterior (simula familias)
            String ap1 = apellidos[r.nextInt(apellidos.length)];
            String ap2 = apellidos[r.nextInt(apellidos.length)];
            boolean hombre = r.nextBoolean();
            String nom = hombre ? nombresH[r.nextInt(nombresH.length)] : nombresM[r.nextInt(nombresM.length)];
            p.setNombres(nom + " " + ap1 + " " + ap2);

            p.setFechaNacimiento(hoy.minusDays(r.nextInt(95 * 365)));

            if (r.nextInt(100) < 85) {
                p.setTipoDocumento("DNI");
                p.setNumeroDocumento(String.valueOf(10000000 + r.nextInt(89999999)));
            } else {
                p.setTipoDocumento("Carné de extranjería");
                p.setNumeroDocumento(String.valueOf(100000000 + r.nextInt(899999999)));
            }

            p.setTelefono("9" + (10000000 + r.nextInt(89999999)));
            p.setDireccion(calles[r.nextInt(calles.length)] + " " + (100 + r.nextInt(1800)));
            p.setDistrito(distritos.get(r.nextInt(distritos.size())));

            pacienteRepo.save(p);
        }
    }
}
