package xualgorithm.mindsolutionsspring.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public boolean validateEmailExists(String email){
        return userRepository.findByEmail(email).isPresent();
    }





}
