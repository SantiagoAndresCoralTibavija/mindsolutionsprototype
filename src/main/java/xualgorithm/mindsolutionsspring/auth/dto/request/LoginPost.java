package xualgorithm.mindsolutionsspring.auth.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginPost {

    @NotBlank
    private String email;
    @NotBlank
    private String password;



}
