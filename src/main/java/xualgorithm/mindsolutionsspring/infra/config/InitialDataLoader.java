package xualgorithm.mindsolutionsspring.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import xualgorithm.mindsolutionsspring.user.application.Role;
import xualgorithm.mindsolutionsspring.user.domain.User;
import xualgorithm.mindsolutionsspring.user.domain.UserRepository;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        long rootCount = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ROOT)
                .count();

        if (rootCount == 0) {
            System.out.println("========================================");
            System.out.println("No se encontró ningún usuario ROOT.");
            System.out.println("Creando usuario ROOT por defecto...");
            System.out.println("========================================");

            // Crear usuario ROOT (datos dummy)
            User user = new User();
            user.setEmail("admin@admin.com");
            user.setUser("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRole(Role.ROOT);
            user.setBloqueado(false);
            userRepository.save(user);

            System.out.println("========================================");
            System.out.println("✓ Usuario ROOT creado exitosamente");
            System.out.println("========================================");
        } else {
            System.out.println("✓ Usuario ROOT ya existe en el sistema");
        }

    }
}
