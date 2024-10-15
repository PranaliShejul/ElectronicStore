package com.example.electronicstore.repositories;

import com.example.electronicstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);

    List<User> findByNameContaining(String keyword);
}
