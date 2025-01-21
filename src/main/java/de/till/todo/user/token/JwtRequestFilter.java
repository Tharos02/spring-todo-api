package de.till.todo.user.token;

import de.till.todo.user.User;
import de.till.todo.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException,
            IOException {
        final String authHeader = request.getHeader("Authorization");

        String jwtToken = null;
        String email = null;

        System.out.println("TEST: " + jwtToken);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            email = jwtUtil.extractEmail(jwtToken);

            System.out.println("TEST-Token: " + jwtToken);
            System.out.println("TEST-Email: " + email);

        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmail(email);
            System.out.println("TEST-User: " + user.toString());
            if (user != null && jwtUtil.validateToken(jwtToken, user)) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                new ArrayList<>()
                        )
                );
            }
        }
        filterChain.doFilter(request, response);
    }
}
