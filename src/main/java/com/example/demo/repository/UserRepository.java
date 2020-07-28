package com.example.demo.repository;

import com.example.demo.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


/***
 * Project Name     : spring-boot-testing
 * Username         : Teten Nugraha
 * Date Time        : 12/18/2019
 * Telegram         : @tennugraha
 */

public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("select u from User u where u.email=?1 and u.password=?2")
    Optional<User> login(String email, String password);
    
    Optional<User> findByEmail(String email);
    
}
