package xualgorithm.mindsolutionsspring.user.application;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xualgorithm.mindsolutionsspring.auth.domain.exception.EmailCreatedException;
import xualgorithm.mindsolutionsspring.auth.domain.exception.PasswordException;
import xualgorithm.mindsolutionsspring.user.domain.User;
import xualgorithm.mindsolutionsspring.user.domain.UserRepository;
import xualgorithm.mindsolutionsspring.user.domain.UserService;
import xualgorithm.mindsolutionsspring.user.domain.exception.UserNotFoundException;
import xualgorithm.mindsolutionsspring.user.dto.request.CreatePost;
import xualgorithm.mindsolutionsspring.user.dto.request.EditPost;
import xualgorithm.mindsolutionsspring.auth.dto.request.RegisterPost;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final PasswordEncoder encoder;
    private final UserRepository repository;
    private final UserService userService;

    @Transactional
    public void createUser(RegisterPost request){
        if(userService.validateEmailExists(request.getEmail().toLowerCase())){
            throw new EmailCreatedException();
        }
        if(!request.getPassword().equals(request.getConfirmpassword())){
            throw new PasswordException("Las contraseñas ingresadas deben coincidir");
        }
        User usuario = new User();
        usuario.setUser(request.getUser());
        usuario.setPassword(encoder.encode(request.getPassword()));
        usuario.setEmail(request.getEmail().toLowerCase());
        usuario.setRole(Role.STUDENT);
        usuario.setBloqueado(false);

        repository.save(usuario);
    }

    @Transactional
    public void createUserDashboard(CreatePost request){
        if(userService.validateEmailExists(request.getEmail().toLowerCase())){
            throw new EmailCreatedException();
        }
        User usuario = new User();
        usuario.setUser(request.getName());
        usuario.setPassword(encoder.encode(request.getPassword()));
        usuario.setEmail(request.getEmail().toLowerCase());
        usuario.setRole(request.getRole());
        usuario.setBloqueado(false);

        repository.save(usuario);
    }

    @Transactional
    public void updateUser(Long id, EditPost request) {
        User usuario = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (request.getName() != null && !request.getName().isBlank()) {
            usuario.setUser(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().toLowerCase();
            if(userService.validateEmailExists(newEmail)){
                throw new EmailCreatedException();
            }
            usuario.setEmail(newEmail);
        }

        if (request.getRole() != null) {
            usuario.setRole(request.getRole());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(encoder.encode(request.getPassword()));
        }
    }

    @Transactional
    public void toggleUserLock(Long id) {
        User usuario = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        usuario.setBloqueado(!usuario.isBloqueado());
    }
}
