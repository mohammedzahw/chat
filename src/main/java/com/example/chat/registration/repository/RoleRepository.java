package com.example.chat.registration.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chat.registration.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByRole(String role);

}
