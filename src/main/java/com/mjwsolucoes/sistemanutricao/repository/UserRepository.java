package com.mjwsolucoes.sistemanutricao.repository;

import com.mjwsolucoes.sistemanutricao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);  // Retorna Optional


    boolean existsByUsername(String username);
}