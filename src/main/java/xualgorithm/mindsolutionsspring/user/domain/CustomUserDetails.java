package xualgorithm.mindsolutionsspring.user.domain;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xualgorithm.mindsolutionsspring.user.application.AuthUser;

@Service
public class CustomUserDetails implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetails(UserRepository repository){
        this.userRepository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("El usuario no se ha encontrado"));

        return new AuthUser(user);
    }


}
