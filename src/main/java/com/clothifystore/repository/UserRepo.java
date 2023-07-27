package com.clothifystore.repository;

import com.clothifystore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {

    boolean existsByEmail(String email);
    User findByUserId(int userID);
    Optional<User> findByEmail(String email);
}
