package xualgorithm.mindsolutionsspring.ai.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xualgorithm.mindsolutionsspring.ai.domain.ConversationContent;

@Repository
public interface ConversationContentRepository extends JpaRepository<ConversationContent, Long> {

}
