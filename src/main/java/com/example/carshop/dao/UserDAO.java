package com.example.carshop.dao;

import com.example.carshop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface UserDAO extends JpaRepository<User, Integer> {
    User save(User user);

    User findUserByUserName(String username);

    List<User> findAll();

    User findUserById(int id);

    User findUserByToken(String token);

}
