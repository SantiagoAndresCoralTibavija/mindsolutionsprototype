package xualgorithm.mindsolutionsspring.infra.security;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import xualgorithm.mindsolutionsspring.auth.dto.request.LoginPost;
import xualgorithm.mindsolutionsspring.user.domain.CustomUserDetails;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetails customUserDetails;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, CustomUserDetails customUserDetails){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetails = customUserDetails;
    }

    public String login(LoginPost request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var userDetails =  customUserDetails.loadUserByUsername(request.getEmail());

        return jwtService.generarToken(userDetails);
    }




}
