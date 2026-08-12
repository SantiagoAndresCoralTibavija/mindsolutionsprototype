package xualgorithm.mindsolutionsspring.infra.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import xualgorithm.mindsolutionsspring.ai.application.AIApplicationService;
import xualgorithm.mindsolutionsspring.ai.domain.Conversation;
import xualgorithm.mindsolutionsspring.ai.domain.exception.ConversationNotFound;
import xualgorithm.mindsolutionsspring.infra.annotation.AuthorizedUUIDConversation;
import xualgorithm.mindsolutionsspring.user.application.AuthUser;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthorizedUUIDConversationResolver implements HandlerMethodArgumentResolver {

    private final AIApplicationService aIApplicationService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter){
        return methodParameter.hasParameterAnnotation(AuthorizedUUIDConversation.class)
                && methodParameter.getParameterType().equals(Conversation.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables =
                (Map<String, String>) webRequest.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
                        RequestAttributes.SCOPE_REQUEST
                );

        String raw = pathVariables != null ? pathVariables.get("conversationUUID") : null;
        if (raw == null) throw new ConversationNotFound();
        UUID conversationUUID;
        try{
            conversationUUID = UUID.fromString(raw);
        }catch (IllegalArgumentException e){
            throw new ConversationNotFound();
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        AuthUser principal =
                (AuthUser) authentication.getPrincipal();


        return aIApplicationService.resolveConversation(conversationUUID, principal.getUserID());
    }




}
