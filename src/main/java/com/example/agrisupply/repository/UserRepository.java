package com.example.agrisupply.repository;

import com.example.agrisupply.model.Role;
import com.example.agrisupply.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByRolesIn(Set<Role> roles); // Find users by roles (e.g., all BUYERs)
}
