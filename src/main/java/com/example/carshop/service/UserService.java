package com.example.carshop.service;

import com.example.carshop.dao.UserDAO;
import com.example.carshop.models.Role;
import com.example.carshop.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserDAO userDAO;
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        return userDAO.save(user);
    }

    public User findUserByLogin(String login) {
        User user = userDAO.findUserByUserName(login);
        return user;
    }

    public User findUserByToken(String token) {
        return userDAO.findUserByToken(token);
    }

    public User findUserById(int id, String header) {
        if (header != null) {
            String clearToken = header.substring(7);
            User userFromDB = userDAO.findUserByToken(clearToken);
            if (userFromDB != null && userFromDB.getId() == id) {
                return userDAO.findUserById(id);
            }
        }
        return null;
    }

    public boolean payForAcc(int accountId, String header) {
        User isLoggedInUser = findUserById(accountId, header);
        isLoggedInUser.setIsPremium(true);
        return saveUser(isLoggedInUser) != null;
    }

    public User saveManagerByAdmin(User userCreatedByAdmin, int adminId, String header) {
        User isLoggedInUser = findUserById(adminId, header);

        if (isLoggedInUser != null && isLoggedInUser.getRole().equals(Role.ADMIN)) {
            userCreatedByAdmin.setPassword(passwordEncoder.encode(userCreatedByAdmin.getPassword()));
            return saveUser(userCreatedByAdmin);
        }
        return null;
    }

    public ResponseEntity<String> login(User user, AuthenticationManager authenticationManager) {
        System.out.println(user.toString());

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(), user.getPassword(), user.getAuthorities()
                );
        System.out.println(usernamePasswordAuthenticationToken);
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if (authenticate != null) {
            String jwtToken = Jwts.builder()
                    .setSubject(authenticate.getName())
                    .signWith(SignatureAlgorithm.HS512, "okten".getBytes(StandardCharsets.UTF_8))
                    .compact();
            System.out.println(jwtToken);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", "Bearer " + jwtToken);
            var userFromDB = userDAO.findUserByUserName(user.getUsername());
            userFromDB.setToken(jwtToken);
            userDAO.save(userFromDB);
            return new ResponseEntity<>("login:)", httpHeaders, HttpStatus.OK);
        }

        return new ResponseEntity<>("bad credentials", HttpStatus.FORBIDDEN);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDAO.findUserByUserName(username);
    }
}