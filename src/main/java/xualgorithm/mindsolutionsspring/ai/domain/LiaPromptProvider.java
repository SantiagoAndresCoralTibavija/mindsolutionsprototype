package xualgorithm.mindsolutionsspring.ai.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LiaPromptProvider {

    private final String systemPrompt;

    public LiaPromptProvider(@Value("classpath:prompts/lia-system.txt") Resource resource) throws IOException {
        this.systemPrompt = resource.getContentAsString(StandardCharsets.UTF_8);
    }

    public String systemPrompt() {
        return this.systemPrompt;
    }

}
