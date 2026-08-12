package xualgorithm.mindsolutionsspring.ai.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromptPost {

    @NotBlank
    private String prompt;

}
