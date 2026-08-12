package xualgorithm.mindsolutionsspring.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import xualgorithm.mindsolutionsspring.user.application.AuthUser;


import java.io.IOException;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public String getTokenFromCookies(HttpServletRequest request){
        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if(cookie.getName().equals("token")){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = getTokenFromCookies(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Si no hay JWT, pero sí hay una autenticación en la SESSION
        if (jwt == null || jwt.isEmpty()) {
            if(authentication != null){
                SecurityContextHolder.clearContext();
                HttpSession session = request.getSession(false);
                if(session != null){
                    session.invalidate();
                }
            }
            filterChain.doFilter(request,response);
            return;
        }


        //Si ya hay JWT y autenticacion en al SESSIONID
        if(authentication != null && authentication.getPrincipal() instanceof  UserDetails){
            String username = jwtService.getSubject(jwt);
            UserDetails userDetails1 = (UserDetails)  authentication.getPrincipal();
            if((userDetails1.getUsername().equals(username) && userDetails1.isEnabled()) && jwtService.JwtValide(jwt)){
                filterChain.doFilter(request,response);
                return;
            }

            //Si la JWT no es valida
            if(!jwtService.JwtValide(jwt)){
                SecurityContextHolder.clearContext();
                HttpSession session = request.getSession(false);
                if(session != null){
                    session.invalidate();
                }
                response.sendRedirect("/ingreso?expirado");
                return;
            }

        }

        //Si hay JWT pero no hay autenticacion en la SESSIONID (O la session no es valida)
        final String username = jwtService.getSubject(jwt);
        if(username != null  && jwtService.JwtValide(jwt)){
            AuthUser userDetails = (AuthUser) userDetailsService.loadUserByUsername(username);
            if(userDetails.isEnabled()){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                filterChain.doFilter(request,response);
            return;
            }

        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }
        response.sendRedirect("/ingreso?invalido");
    }








}
