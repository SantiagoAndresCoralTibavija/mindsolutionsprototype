package xualgorithm.mindsolutionsspring.user.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import xualgorithm.mindsolutionsspring.user.application.Role;

@Getter
@Setter
public class CreatePost {

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotNull
    private Role role;

    @NotBlank
    private String password;

}
