package xualgorithm.mindsolutionsspring.ai.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import xualgorithm.mindsolutionsspring.ai.application.AIApplicationService;
import xualgorithm.mindsolutionsspring.ai.domain.Conversation;
import xualgorithm.mindsolutionsspring.ai.domain.exception.FillPromptException;
import xualgorithm.mindsolutionsspring.ai.dto.request.PromptPost;
import xualgorithm.mindsolutionsspring.infra.annotation.AuthorizedUUIDConversation;
import xualgorithm.mindsolutionsspring.user.application.AuthUser;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AIController {

    private static final Logger log = LoggerFactory.getLogger(AIController.class);

    private final AIApplicationService aiApplicationService;

    @PostMapping("/ai/chat/{conversationUUID}")
    public String postPrompt(@AuthorizedUUIDConversation Conversation conversation, @ModelAttribute @Valid PromptPost prompt, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("message", new FillPromptException().getMessage());
            return "shared/_alerts :: toast-info";
        }

        String userMessage = prompt.getPrompt();
        model.addAttribute("userMessage", userMessage);

        try {
            model.addAttribute("aiMessage", aiApplicationService.requestPrompt(userMessage, conversation.getId()));
        } catch (Exception e) {
            log.error("Fallo al procesar el prompt de la conversacion {}", conversation.getId(), e);
            model.addAttribute("aiMessage", "Lo siento, ocurrio un error al procesar tu solicitud.");
        }

        return "chat/_message_pair :: pair";
    }

    @PostMapping("/ai/chat/new")
    public String postCreateConversation(@ModelAttribute @Valid PromptPost prompt, BindingResult result, @AuthenticationPrincipal AuthUser authUser, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("message", new FillPromptException().getMessage());
            return "shared/_alerts :: toast-info";
        }

        UUID uuid = aiApplicationService.createConversation(prompt.getPrompt(), authUser.getUserID());

        return "redirect:/dashboard/chatbot/" + uuid;
    }

    @PostMapping("/ai/chat/{conversationUUID}/delete")
    public String deleteConversation(@AuthorizedUUIDConversation Conversation conversation) {
        aiApplicationService.deleteConversation(conversation.getId());
        return "redirect:/dashboard/chatbot";
    }
}
