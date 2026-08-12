package xualgorithm.mindsolutionsspring.user.dto.request;


import lombok.Getter;
import lombok.Setter;
import xualgorithm.mindsolutionsspring.user.application.Role;

@Getter
@Setter
public class EditPost {

    private String name;

    private String password;

    private String email;

    private Role role;

    private Boolean bloqueado;




}
