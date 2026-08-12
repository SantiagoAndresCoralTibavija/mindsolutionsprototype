package xualgorithm.mindsolutionsspring.infra.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xualgorithm.mindsolutionsspring.user.domain.User;
import xualgorithm.mindsolutionsspring.user.domain.UserRepository;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService{

    private final UserRepository userRepository;
    private final Key secretKey;
    private final long jwtExpirationMs;


    public JwtService(UserRepository userRepository, @Value("${project.jwt.key}") String key, @Value("${project.jwt.expiration}") long jwtExpirationMs) {
        this.userRepository = userRepository;
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
        this.jwtExpirationMs = jwtExpirationMs;
    }


    public long getExpirationSeconds() {
        return jwtExpirationMs / 1000;
    }


    public String generarToken(UserDetails userDetails){
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->
                new IllegalStateException("No se encontro el email ingresado"));


        Map<String, Object> claims = new HashMap<>();

        claims.put("uid", user.getID());
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean JwtValide(String JWT){
        try{
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(JWT);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }


    public String getSubject(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


}
