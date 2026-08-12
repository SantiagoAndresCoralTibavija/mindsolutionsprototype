package xualgorithm.mindsolutionsspring.ai.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xualgorithm.mindsolutionsspring.ai.domain.repository.AIRepository;
import xualgorithm.mindsolutionsspring.ai.domain.exception.ConversationNotFound;
import xualgorithm.mindsolutionsspring.ai.dto.response.ConversationContentView;
import xualgorithm.mindsolutionsspring.ai.dto.response.ConversationView;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AIQueryService {

    private final AIRepository aIRepository;

    public ConversationView getConversationDTO(UUID conversationId){
        return new ConversationView(aIRepository.findByUUIDWithRelations(conversationId).orElseThrow(ConversationNotFound::new));
    }

    public List<ConversationView> getConversationListByUser(Long userId){
        return aIRepository.findByUserAndOrderByLastMessageSentAtDesc(userId).stream().map(ConversationView::new).toList();
    }

    public List<ConversationContentView> getConversationContent(UUID conversationUUID){
        return aIRepository.findConversationContentByConversationUUIDOrderBySentAtAsc(conversationUUID).stream().map(ConversationContentView::new).toList();
    }





}
