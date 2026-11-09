package pe.ollantay.hcweb.web;

import org.springframework.web.bind.annotation.*;
import pe.ollantay.hcweb.model.Paciente;
import pe.ollantay.hcweb.service.PacienteService;

import java.util.*;

/**
 * Endpoints que responden en JSON para los avisos en vivo del formulario
 * (HC duplicada, documento duplicado y coincidencias de nombres).
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final PacienteService service;

    public ApiController(PacienteService service) {
        this.service = service;
    }

    @GetMapping("/existe-hc")
    public Map<String, Boolean> existeHc(@RequestParam String hc,
                                         @RequestParam(required = false) Long id) {
        return Map.of("existe", service.existeHc(hc, id));
    }

    @GetMapping("/existe-doc")
    public Map<String, Boolean> existeDoc(@RequestParam String num,
                                          @RequestParam(required = false) Long id) {
        return Map.of("existe", service.existeDocumento(num, id));
    }

    @GetMapping("/coincidencias")
    public List<Map<String, String>> coincidencias(@RequestParam String nombres,
                                                   @RequestParam(required = false) Long id) {
        List<Map<String, String>> salida = new ArrayList<>();
        for (Paciente p : service.coincidencias(nombres, id)) {
            salida.add(Map.of("hc", p.getHc(), "nombres", p.getNombres()));
        }
        return salida;
    }
}
