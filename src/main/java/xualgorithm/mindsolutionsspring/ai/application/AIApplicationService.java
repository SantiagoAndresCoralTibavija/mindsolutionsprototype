package xualgorithm.mindsolutionsspring.ai.application;

import jakarta.transaction.Transactional;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import xualgorithm.mindsolutionsspring.ai.domain.*;
import xualgorithm.mindsolutionsspring.ai.domain.exception.BadResponseException;
import xualgorithm.mindsolutionsspring.ai.domain.exception.ConversationNotFound;
import xualgorithm.mindsolutionsspring.ai.domain.repository.AIRepository;
import xualgorithm.mindsolutionsspring.ai.domain.repository.ConversationContentRepository;
import xualgorithm.mindsolutionsspring.infra.exception.AccessForbidden;
import xualgorithm.mindsolutionsspring.user.domain.User;
import xualgorithm.mindsolutionsspring.user.domain.UserRepository;
import xualgorithm.mindsolutionsspring.user.domain.exception.UserNotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service

public class AIApplicationService {


    private final UserRepository userRepository;
    private final ChatClient chatClient;
    private final AIRepository aiRepository;
    private final AIChatService aiChatService;
    private final ConversationContentRepository conversationContentRepository;
    private final LiaPromptProvider liaPromptProvider;

    public AIApplicationService(UserRepository userRepository, AIChatService aiChatService, ChatClient.Builder chatClient, AIRepository aiRepository, ConversationContentRepository conversationContentRepository, LiaPromptProvider liaPromptProvider) {
        this.userRepository = userRepository;
        this.chatClient = chatClient.build();
        this.aiRepository = aiRepository;
        this.aiChatService = aiChatService;
        this.conversationContentRepository = conversationContentRepository;
        this.liaPromptProvider = liaPromptProvider;
    }

    @Transactional
    public UUID createConversation(String prompt, Long userId){
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Conversation conversation = new Conversation();


        String title = generateTitle(prompt);
        if(title == null || title.isBlank()){
            throw new BadResponseException();
        }
        title = title.replaceAll("[\"\\n]", "").trim();
        conversation.setTitle(title);
        conversation.setUser(user);
        conversation.setCreatedAt(Instant.now());
        conversation.setLastMessageSentAt(Instant.now());

        aiRepository.save(conversation);

        List<ConversationContent> conversationContentList = new ArrayList<>();

        ConversationContent systemPrompt = new ConversationContent();
        systemPrompt.setConversation(conversation);
        systemPrompt.setAuthor(AuthorConversationContent.SYSTEM);
        systemPrompt.setText(liaPromptProvider.systemPrompt());
        systemPrompt.setSentAt(Instant.now());

        conversationContentRepository.save(systemPrompt);

        conversationContentList.add(systemPrompt);

        ConversationContent userPrompt =  new ConversationContent();
        userPrompt.setConversation(conversation);
        userPrompt.setAuthor(AuthorConversationContent.USER);
        userPrompt.setText(prompt);
        userPrompt.setSentAt(Instant.now());

        conversationContentRepository.save(userPrompt);
        conversationContentList.add(userPrompt);

        ChatResponse response = chatClient.prompt(new Prompt(aiChatService.mapToMessage(conversationContentList, prompt)))
                .call().chatResponse();

        if(response == null){
            throw new BadResponseException();
        }

        ConversationContent assistantResponse = new ConversationContent();
        assistantResponse.setText(response.getResult().getOutput().getText());
        assistantResponse.setAuthor(AuthorConversationContent.AI);
        assistantResponse.setConversation(conversation);
        assistantResponse.setSentAt(Instant.now());

        conversationContentRepository.save(assistantResponse);

        return conversation.getId();
    }

    @Transactional
    public String requestPrompt(String prompt, UUID uuid){

        Conversation conversation = aiRepository.findByUUIDWithRelations(uuid).orElseThrow(ConversationNotFound::new);

        List<Message> messages = aiChatService.mapToMessage(aiRepository.findConversationContentByConversationUUIDOrderBySentAtAsc(uuid), prompt);

        messages.add(new UserMessage(prompt));

        ChatResponse response = chatClient
                .prompt(new Prompt(messages))
                .call()
                .chatResponse();

        if(response ==  null){
            throw new BadResponseException();
        }

        String responseChat = response.getResult().getOutput().getText();

        ConversationContent userMessage = new ConversationContent();

        userMessage.setConversation(conversation);
        userMessage.setAuthor(AuthorConversationContent.USER);
        userMessage.setText(prompt);
        userMessage.setSentAt(Instant.now());

        conversationContentRepository.save(userMessage);

        ConversationContent assistantMessage = new ConversationContent();

        assistantMessage.setConversation(conversation);
        assistantMessage.setText(responseChat);
        assistantMessage.setAuthor(AuthorConversationContent.AI);
        assistantMessage.setSentAt(Instant.now());

        conversationContentRepository.save(assistantMessage);

        conversation.setLastMessageSentAt(Instant.now());

        return responseChat;
    }

    public Conversation resolveConversation(UUID uuid, Long userId){
        Conversation conversation = aiRepository.findByUUIDWithRelations(uuid).orElseThrow(ConversationNotFound::new);

        if(!aiChatService.ensureCanAccess(conversation, userId)){
            throw new AccessForbidden("No tienes acceso a esta conversacion");
        }

        return conversation;
    }

    @Transactional
    public void deleteConversation(UUID uuid) {
        Conversation conversation = aiRepository.findByUUIDWithRelations(uuid).orElseThrow(ConversationNotFound::new);

        List<ConversationContent> conversationContent = aiRepository.findConversationContentByConversationUUIDOrderBySentAtAsc(uuid);

        conversationContentRepository.deleteAll(conversationContent);
        aiRepository.delete(conversation);
    }

    public String generateTitle(String userPrompt){
        List<Message> systemPrompt = new ArrayList<>();

        systemPrompt.add(new SystemMessage("Eres un generador de títulos para conversaciones."+
                "Tu tarea es leer el primer mensaje del usuario y crear un título corto, claro y relevante que resuma el tema principal de ese mensaje. "+
                "El título debe sonar natural, atractivo y coherente con el contenido del mensaje. "+
                "No incluyas comillas, saludos ni texto adicional. "+
                "Devuelve únicamente el título final."));

        systemPrompt.add(new UserMessage(userPrompt));

        ChatResponse response = chatClient
                .prompt(new Prompt(systemPrompt))
                .call()
                .chatResponse();

        return response.getResult().getOutput().getText();
    }
}
