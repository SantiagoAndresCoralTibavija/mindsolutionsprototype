package xualgorithm.mindsolutionsspring.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xualgorithm.mindsolutionsspring.user.domain.UserRepository;
import xualgorithm.mindsolutionsspring.user.domain.exception.UserNotFoundException;
import xualgorithm.mindsolutionsspring.user.dto.response.UserView;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public UserView getUserDTO(Long userId){
        return new UserView(userRepository.findById(userId).orElseThrow(UserNotFoundException::new));
    }


    public List<UserView> getAllUserList(){
        return userRepository.findAllWithRelations().stream().map(UserView::new).toList();
    }



}
