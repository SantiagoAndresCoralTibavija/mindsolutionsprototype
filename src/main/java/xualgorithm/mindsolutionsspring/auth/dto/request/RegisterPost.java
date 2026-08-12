package xualgorithm.mindsolutionsspring.auth.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterPost {

    @NotBlank
    private String user;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmpassword;

}
