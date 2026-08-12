package xualgorithm.mindsolutionsspring.infra.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xualgorithm.mindsolutionsspring.infra.security.JwtAuthFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] STATIC_PATHS = {
            "/css/**", "/js/**", "/img/**", "/audio/**", "/vendor/**", "/fonts/**"
    };


    @Bean
    public SecurityFilterChain filterChain (HttpSecurity security, JwtAuthFilter jwtAuthFilter) throws Exception {

        security
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(STATIC_PATHS).permitAll()
                        .requestMatchers("/favicon.ico", "/", "/ingreso", "/login", "/registro", "/register").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(((request, response, authException) -> {
                            String path = request.getRequestURI();
                            boolean isStatic = path.startsWith("/css") || path.startsWith("/js")
                                    || path.startsWith("/img") || path.startsWith("/audio")
                                    || path.startsWith("/vendor") || path.startsWith("/fonts");
                            if (isStatic) {
                                response.setStatus(404);
                            } else if (request.getHeader("HX-Request") != null) {
                                response.setHeader("HX-Redirect", "/ingreso");
                                response.setStatus(200);
                            } else {
                                response.sendRedirect("/ingreso");
                            }
                        })))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "token")
                        .logoutSuccessUrl("/ingreso?logout")
                        .permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return security.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws Exception{
        return configuration.getAuthenticationManager();
    }
}
