package xualgorithm.mindsolutionsspring.ai.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xualgorithm.mindsolutionsspring.ai.domain.Conversation;
import xualgorithm.mindsolutionsspring.ai.domain.ConversationContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AIRepository extends JpaRepository<Conversation, UUID> {

    @Query("""
SELECT c FROM Conversation c
LEFT JOIN FETCH c.user u
WHERE c.id =:uuid
""")
    Optional<Conversation> findByUUIDWithRelations(@Param("uuid") UUID uuid);

@Query("""
SELECT c FROM Conversation c
LEFT JOIN FETCH c.user u
WHERE u.ID =:userId
ORDER BY c.lastMessageSentAt DESC
""")
    List<Conversation> findByUserAndOrderByLastMessageSentAtDesc(@Param("userId") Long userId);


@Query("""
SELECT con FROM ConversationContent con
WHERE con.conversation.id =:conversationUUID
ORDER BY con.sentAt ASC
""")
    List<ConversationContent> findConversationContentByConversationUUIDOrderBySentAtAsc(@Param("conversationUUID") UUID conversationUUID);
}
