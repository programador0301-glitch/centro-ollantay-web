package pe.ollantay.hcweb.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Agrega a todas las vistas el usuario actual y si es administrador,
 * para mostrar/ocultar opciones en las páginas.
 */
@ControllerAdvice
public class GlobalModel {

    @ModelAttribute("usuarioActual")
    public String usuarioActual() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return (a != null) ? a.getName() : "";
    }

    @ModelAttribute("esAdmin")
    public boolean esAdmin() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) return false;
        return a.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
    }
}
