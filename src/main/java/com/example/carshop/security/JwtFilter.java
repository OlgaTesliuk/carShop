package com.example.carshop.security;

import com.example.carshop.dao.UserDAO;
import com.example.carshop.models.User;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private UserDAO userDAO;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.replace("Bearer ", "");

            String decodedData = Jwts.parser()
                    .setSigningKey("okten".getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            User client = userDAO.findUserByUserName(decodedData);
            if (client != null) {
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        client.getUsername(),
                                        client.getPassword(),
                                        client.getAuthorities()
                                )
                        );
            }

        }
        filterChain.doFilter(request, response);
    }
}
