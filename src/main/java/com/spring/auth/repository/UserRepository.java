package com.spring.auth.repository;


import com.spring.auth.model.UserName;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserName, Long> {
    UserName findByUsername(String username);
}