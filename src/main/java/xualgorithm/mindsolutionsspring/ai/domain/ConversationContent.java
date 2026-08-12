package xualgorithm.mindsolutionsspring.ai.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.Instant;

@Entity(name = "ConversationContent")
@Table(name = "conversation_content")
@Getter
@Setter
public class ConversationContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "author")
    private AuthorConversationContent author;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @NotNull
    private Instant sentAt;
}
