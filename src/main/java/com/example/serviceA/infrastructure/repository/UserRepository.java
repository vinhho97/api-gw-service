package com.example.serviceA.infrastructure.repository;

import com.example.serviceA.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
}
