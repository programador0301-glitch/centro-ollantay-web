package pe.ollantay.hcweb.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.ollantay.hcweb.model.Usuario;
import pe.ollantay.hcweb.service.UsuarioService;

/**
 * Gestión de usuarios (solo ADMIN; protegido en SecurityConfig).
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("usuarios", service.listar());
        return "usuarios";
    }

    @PostMapping
    public String crear(@RequestParam String usuario,
                        @RequestParam String clave,
                        @RequestParam String rol,
                        RedirectAttributes ra) {
        if (usuario.isBlank() || clave.isBlank()) {
            ra.addFlashAttribute("error", "Ingrese usuario y contraseña.");
        } else if (service.existe(usuario.trim())) {
            ra.addFlashAttribute("error", "Ya existe un usuario con ese nombre.");
        } else {
            service.crear(usuario, clave, rol);
            ra.addFlashAttribute("ok", "Usuario creado correctamente.");
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/clave")
    public String cambiarClave(@PathVariable Long id,
                               @RequestParam String clave,
                               RedirectAttributes ra) {
        if (clave.isBlank()) {
            ra.addFlashAttribute("error", "La nueva contraseña no puede estar vacía.");
        } else {
            service.cambiarClave(id, clave);
            ra.addFlashAttribute("ok", "Contraseña actualizada.");
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        Usuario u = service.porId(id);
        if (u != null && u.esAdmin() && service.contarAdmins() <= 1) {
            ra.addFlashAttribute("error", "No se puede eliminar el único administrador del sistema.");
        } else {
            service.eliminar(id);
            ra.addFlashAttribute("ok", "Usuario eliminado.");
        }
        return "redirect:/usuarios";
    }
}
