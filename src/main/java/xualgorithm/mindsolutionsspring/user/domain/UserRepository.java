package xualgorithm.mindsolutionsspring.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUser(String user);

    @Query("""
SELECT u FROM users u
""")
    List<User> findAllWithRelations();
}
