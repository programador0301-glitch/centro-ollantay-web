package pe.ollantay.hcweb.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String inicio() {
        return "redirect:/pacientes";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
