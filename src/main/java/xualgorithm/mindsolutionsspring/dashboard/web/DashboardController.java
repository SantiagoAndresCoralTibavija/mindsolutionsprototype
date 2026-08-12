package xualgorithm.mindsolutionsspring.dashboard.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xualgorithm.mindsolutionsspring.ai.application.AIQueryService;
import xualgorithm.mindsolutionsspring.ai.domain.Conversation;
import xualgorithm.mindsolutionsspring.ai.dto.response.ConversationContentView;
import xualgorithm.mindsolutionsspring.ai.dto.response.ConversationView;
import xualgorithm.mindsolutionsspring.infra.annotation.AuthorizedUUIDConversation;
import xualgorithm.mindsolutionsspring.user.application.AuthUser;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AIQueryService aiQueryService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard/index";
    }

    @GetMapping("/dashboard/chatbot")
    public String getChatbotFragment(@AuthenticationPrincipal AuthUser authUser, Model model) {
        model.addAttribute("conversations", aiQueryService.getConversationListByUser(authUser.getUserID()));
        model.addAttribute("greeting", greetingForNow());
        return "chat/index :: page";
    }

    private String greetingForNow() {
        int hour = LocalTime.now().getHour();

        if (hour < 6) return "Es de madrugada";
        if (hour < 12) return "Buenos dias";
        if (hour < 19) return "Buenas tardes";
        return "Buenas noches";
    }

    @GetMapping("/dashboard/chatbot/{conversationUUID}")
    public String getChatbotConversationFragment(@AuthorizedUUIDConversation Conversation conversation,
                                                 @AuthenticationPrincipal AuthUser authUser,
                                                 Model model) {

        ConversationView view = aiQueryService.getConversationDTO(conversation.getId());
        List<ConversationContentView> content = aiQueryService.getConversationContent(conversation.getId());

        model.addAttribute("conversation", view);
        model.addAttribute("conversationContent", content);
        model.addAttribute("activeConversationUuid", conversation.getId());
        model.addAttribute("conversations", aiQueryService.getConversationListByUser(authUser.getUserID()));

        return "chat/conversation :: page";
    }

    @GetMapping("/dashboard/meditations")
    public String getMeditationsFragment() {
        return "meditations/index :: page";
    }

    @GetMapping("/dashboard/sessions")
    public String getSessionsFragment() {
        return "sessions/index :: page";
    }

    @GetMapping("/dashboard/right-panel/chatbot")
    public String getRightPanelChatbot(@AuthenticationPrincipal AuthUser authUser,
                                       @RequestParam(name = "active", required = false) UUID active,
                                       Model model) {

        model.addAttribute("conversations", aiQueryService.getConversationListByUser(authUser.getUserID()));
        model.addAttribute("activeConversationUuid", active);

        return "chat/_sidebar :: panel";
    }

    @GetMapping("/dashboard/right-panel/meditations")
    public String getRightPanelMeditations() {
        return "meditations/_sidebar :: panel";
    }

    @GetMapping("/dashboard/right-panel/sessions")
    public String getRightPanelSessions() {
        return "sessions/_sidebar :: panel";
    }
}
