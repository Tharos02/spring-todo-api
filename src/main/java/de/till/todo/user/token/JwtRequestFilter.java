package de.till.todo.user.token;

import de.till.todo.user.User;
import de.till.todo.user.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Service
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException,
            IOException {

        if(request.getRequestURI().contains("/login") || request.getRequestURI().contains("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        final Cookie accesTokenCookie = request.getCookies() == null ? null : Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("accessToken"))
                .findFirst()
                .orElse(null);

        String jwtToken = null;
        String email = null;

        if(accesTokenCookie != null) {
            jwtToken = accesTokenCookie.getValue();

            try {
                email = jwtUtil.extractEmail(jwtToken);
            } catch (ExpiredJwtException e) {
                sendUnauthorizedResponse(response, "Token is expired");
                return;
            }
        } else {
            sendUnauthorizedResponse(response, "No accessToken cookie found");
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmail(email);
            if (user != null && jwtUtil.validateToken(jwtToken, user)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        new ArrayList<>()
                );

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    public void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
