package com.clothifystore.repository;

import com.clothifystore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Integer> {
    int countByUsername(String username);
    int countByEmail(String email);

    User findByUserId(int userID);
}
