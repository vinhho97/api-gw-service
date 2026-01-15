package com.example.serviceA.infrastructure.repository;

import com.example.serviceA.domain.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepository implements UserRepository {
    @Override
    public Optional<User> findByUsername(String username) {
        // Hardcoded user for simplicity
        if ("user1".equals(username)) {
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setUsername("user1");
            user.setPassword("password");
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
