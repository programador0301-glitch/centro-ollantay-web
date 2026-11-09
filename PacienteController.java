package pe.ollantay.hcweb.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.ollantay.hcweb.model.Paciente;
import pe.ollantay.hcweb.service.PacienteService;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    // ---------- LISTA + FILTROS ----------
    @GetMapping
    public String lista(@RequestParam(required = false) String buscar,
                        @RequestParam(required = false) String distrito,
                        Model model) {
        List<Paciente> lista;
        if (distrito != null && !distrito.isBlank()) {
            lista = service.porDistrito(distrito);
        } else {
            lista = service.buscar(buscar);
        }
        model.addAttribute("pacientes", lista);
        model.addAttribute("total", lista.size());
        model.addAttribute("buscar", buscar == null ? "" : buscar);
        model.addAttribute("distritoSel", distrito == null ? "" : distrito);
        model.addAttribute("distritos", PacienteService.DISTRITOS);
        return "pacientes";
    }

    // ---------- FORMULARIO NUEVO ----------
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("paciente", new Paciente());
        prepararForm(model);
        return "form";
    }

    // ---------- FORMULARIO EDITAR ----------
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Paciente p = service.porId(id);
        if (p == null) {
            ra.addFlashAttribute("error", "El paciente no existe.");
            return "redirect:/pacientes";
        }
        model.addAttribute("paciente", p);
        prepararForm(model);
        return "form";
    }

    // ---------- GUARDAR (crear o actualizar) ----------
    @PostMapping
    public String guardar(@ModelAttribute Paciente paciente, Model model, RedirectAttributes ra) {
        String err = validar(paciente);
        if (err != null) {
            model.addAttribute("paciente", paciente);
            model.addAttribute("error", err);
            prepararForm(model);
            return "form";
        }
        boolean nuevo = (paciente.getId() == null);
        service.guardar(paciente);
        ra.addFlashAttribute("ok", nuevo ? "Paciente agregado correctamente."
                                         : "Paciente actualizado correctamente.");
        return "redirect:/pacientes";
    }

    // ---------- ELIMINAR (solo ADMIN, protegido en SecurityConfig) ----------
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        service.eliminar(id);
        ra.addFlashAttribute("ok", "Paciente eliminado.");
        return "redirect:/pacientes";
    }

    // ---------- EXPORTAR A EXCEL (CSV) ----------
    @GetMapping("/exportar")
    public void exportar(@RequestParam(required = false) String buscar,
                        @RequestParam(required = false) String distrito,
                        HttpServletResponse resp) throws IOException {
        List<Paciente> lista;
        if (distrito != null && !distrito.isBlank()) lista = service.porDistrito(distrito);
        else lista = service.buscar(buscar);

        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"pacientes_" + java.time.LocalDate.now() + ".csv\"");

        try (Writer w = new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8)) {
            w.write('﻿'); // BOM para que Excel muestre bien los acentos
            w.write("HC;Nombres;F. Nacimiento;Edad;Tipo doc;N documento;Telefono;Direccion;Distrito\r\n");
            for (Paciente p : lista) {
                w.write(String.join(";",
                        c(p.getHc()), c(p.getNombres()), c(p.getFechaTexto()),
                        c(p.getEdad() < 0 ? "" : String.valueOf(p.getEdad())),
                        c(p.getTipoDocumento()), c(p.getNumeroDocumento()),
                        c(p.getTelefono()), c(p.getDireccion()), c(p.getDistrito())));
                w.write("\r\n");
            }
        }
    }

    // ---------- RESUMEN (dashboard) ----------
    @GetMapping("/resumen")
    public String resumen(Model model) {
        List<Paciente> todos = service.listarTodos();
        int total = todos.size();
        int conDni = 0, conCe = 0;

        Map<String, Integer> porDistrito = new LinkedHashMap<>();
        Map<String, Integer> porEdad = new LinkedHashMap<>();
        porEdad.put("0-11", 0); porEdad.put("12-17", 0); porEdad.put("18-39", 0);
        porEdad.put("40-59", 0); porEdad.put("60+", 0);

        for (Paciente p : todos) {
            if ("DNI".equals(p.getTipoDocumento())) conDni++;
            else if (p.getTipoDocumento() != null) conCe++;

            if (p.getDistrito() != null && !p.getDistrito().isBlank()) {
                porDistrito.merge(p.getDistrito(), 1, Integer::sum);
            }
            int e = p.getEdad();
            if (e >= 0) {
                String r = e <= 11 ? "0-11" : e <= 17 ? "12-17" : e <= 39 ? "18-39"
                        : e <= 59 ? "40-59" : "60+";
                porEdad.merge(r, 1, Integer::sum);
            }
        }

        // Distritos ordenados de mayor a menor
        LinkedHashMap<String, Integer> distritoOrden = new LinkedHashMap<>();
        porDistrito.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(en -> distritoOrden.put(en.getKey(), en.getValue()));

        Map<String, Integer> porTipo = new LinkedHashMap<>();
        porTipo.put("DNI", conDni);
        porTipo.put("Carné ext.", conCe);

        model.addAttribute("total", total);
        model.addAttribute("conDni", conDni);
        model.addAttribute("conCe", conCe);
        model.addAttribute("numDistritos", distritoOrden.size());
        model.addAttribute("porTipo", porTipo);
        model.addAttribute("porEdad", porEdad);
        model.addAttribute("porDistrito", distritoOrden);
        model.addAttribute("maxTipo", maximo(porTipo));
        model.addAttribute("maxEdad", maximo(porEdad));
        model.addAttribute("maxDistrito", maximo(distritoOrden));
        return "resumen";
    }

    // ---------- helpers ----------
    private void prepararForm(Model model) {
        model.addAttribute("distritos", PacienteService.DISTRITOS);
        model.addAttribute("tipos", PacienteService.TIPOS_DOC);
    }

    private int maximo(Map<String, Integer> m) {
        int max = 1;
        for (int v : m.values()) max = Math.max(max, v);
        return max;
    }

    private String c(String s) {
        if (s == null) return "";
        if (s.contains(";") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    /** Valida y devuelve un mensaje de error, o null si todo está bien. */
    private String validar(Paciente p) {
        if (p.getHc() == null || p.getHc().isBlank()) return "La HC es obligatoria.";
        if (p.getNombres() == null || p.getNombres().isBlank()) return "Los nombres son obligatorios.";

        if (service.existeHc(p.getHc(), p.getId()))
            return "La HC \"" + p.getHc() + "\" ya está registrada en otro paciente.";

        String num = p.getNumeroDocumento() == null ? "" : p.getNumeroDocumento().trim();
        if (num.isEmpty()) return "El número de documento es obligatorio.";
        if (!num.matches("\\d+")) return "El número de documento solo debe tener números.";
        boolean esDni = "DNI".equals(p.getTipoDocumento());
        if (esDni && num.length() != 8) return "El DNI debe tener exactamente 8 dígitos.";
        if (!esDni && num.length() != 9) return "El carné de extranjería debe tener exactamente 9 dígitos.";
        if (service.existeDocumento(num, p.getId()))
            return "El documento \"" + num + "\" ya está registrado en otro paciente.";

        String tel = p.getTelefono() == null ? "" : p.getTelefono().trim();
        if (!tel.isEmpty() && !tel.matches("\\d{6,15}"))
            return "El teléfono debe tener solo números (entre 6 y 15 dígitos).";

        return null;
    }
}
