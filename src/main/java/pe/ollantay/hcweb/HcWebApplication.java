package pe.ollantay.hcweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion web.
 * Al ejecutar, levanta un servidor en http://localhost:8080
 */
@SpringBootApplication
public class HcWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(HcWebApplication.class, args);
    }
}
