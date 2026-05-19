package AuthAPI.AuthAPI.infra.segurity;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.AuthorizeHttpRequestsDsl;
import org.springframework.security.config.annotation.web.HttpSecurityDsl;
import org.springframework.security.config.annotation.web.PasswordManagementDsl;
import org.springframework.security.config.annotation.web.SecurityMarker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
    private final SecurityFilter securityFilter;
    public SecurityConfigurations(SecurityFilter securityFilter){
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <-- ADICIONAR AQUI
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                .authorizeHttpRequests(req-> {
                    req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/auth/esqueci-senha").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/auth/reset-senha").permitAll();
                    // CORREÇÃO 1: Mudamos de "register" para "registrar"
                    req.requestMatchers(HttpMethod.POST, "/api/usuario/registrar").permitAll();
                    // CORREÇÃO 2: Mudamos para hasRole para bater com o ROLE_ADMIN da sua entidade
                    req.requestMatchers(HttpMethod.POST, "/api/usuario/registrar/admin").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/usuario/admin/listar").hasRole("ADMIN");

                    req.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter,UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("*")); // Em ambiente controlado "*"
        configuration.setAllowedOrigins(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedOrigins(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
