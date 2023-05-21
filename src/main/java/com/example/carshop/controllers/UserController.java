package com.example.carshop.controllers;

import com.example.carshop.dao.UserDAO;
import com.example.carshop.models.Role;
import com.example.carshop.models.User;
import com.example.carshop.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor

public class UserController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private UserDAO userDAO;
    private AuthenticationManager authenticationManager;


    @PostMapping("/clients/save")
    public ResponseEntity<User> saveClient(@RequestBody User user) {
        user.setEmail(user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userDAO.save(user);
        return new ResponseEntity<>(savedUser, HttpStatusCode.valueOf(200));
    }


    @DeleteMapping("/clients/{id}")
    public ResponseEntity<List<User>> deleteUser(@PathVariable int id) {
        userDAO.deleteById(id);
        return new ResponseEntity<>(userDAO.findAll(), HttpStatus.OK);
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registrationNewUser(@RequestBody User user) {
        if (user.getRole().equals(Role.USER)) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (userService.saveUser(user) != null) {
                return new ResponseEntity<String>("Success registration!", HttpStatus.OK);
            }
        }
        return new ResponseEntity<String>("Error, please , try again :(", HttpStatus.EXPECTATION_FAILED);
    }

    @PostMapping("/clients/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        return userService.login(user, authenticationManager);

    }

    @PostMapping("/registration/CreateManager/{id}")
    public ResponseEntity<String> registrationNewUser(@RequestBody User user, @RequestHeader("Authorization") String header, @PathVariable int id) {
        User userFroService = userService.saveManagerByAdmin(user, id, header);
        if (userFroService != null) {
            return new ResponseEntity<String>("Success registration!", HttpStatus.OK);
        }
        return new ResponseEntity<String>("Error, please , try again :(", HttpStatus.EXPECTATION_FAILED);
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity pay(@PathVariable int id, @RequestHeader("Authorization") String header) {
        boolean isPayed = userService.payForAcc(id, header);
        if (isPayed) {
            return new ResponseEntity("payed", HttpStatus.OK);
        }
        return new ResponseEntity("error", HttpStatus.BAD_REQUEST);
    }
}

