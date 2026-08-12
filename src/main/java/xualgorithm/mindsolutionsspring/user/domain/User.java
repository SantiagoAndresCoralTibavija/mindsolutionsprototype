package xualgorithm.mindsolutionsspring.user.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import xualgorithm.mindsolutionsspring.ai.domain.Conversation;
import xualgorithm.mindsolutionsspring.user.application.Role;

import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@Table(name = "users")
@EqualsAndHashCode(of = "ID")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long ID;

    @Column(name = "username", nullable = false)
    private String user;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean bloqueado;
}
