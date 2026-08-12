package xualgorithm.mindsolutionsspring.ai.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import xualgorithm.mindsolutionsspring.user.domain.User;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name = "Conversation")
@Table(name = "Conversation")
@Getter
@Setter
public class Conversation {

    @Id
    private UUID id;

    @NotBlank
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private Instant lastMessageSentAt;

    @PrePersist
    public void generateUUID(){
        if(this.id ==  null){
            this.id = UUID.randomUUID();
        }
    }

    @NotNull
    private Instant createdAt;
}
