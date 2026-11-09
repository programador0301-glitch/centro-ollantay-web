package pe.ollantay.hcweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de seguridad: login por formulario y permisos por rol.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos publicos
                .requestMatchers("/login", "/css/**", "/img/**", "/js/**").permitAll()
                // Solo ADMIN: gestion de usuarios y eliminar pacientes
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/pacientes/*/eliminar").hasRole("ADMIN")
                // El resto requiere estar logueado
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/pacientes", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?salio")
                .permitAll()
            );
        return http.build();
    }
}
