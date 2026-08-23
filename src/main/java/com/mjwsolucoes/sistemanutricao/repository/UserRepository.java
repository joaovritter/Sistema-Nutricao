package com.mjwsolucoes.sistemanutricao.repository;

import com.mjwsolucoes.sistemanutricao.model.Role;
import com.mjwsolucoes.sistemanutricao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    /** Solicitações de acesso ainda não avaliadas, das mais antigas para as mais novas. */
    List<User> findByRoleOrderByCriadoEmAsc(Role role);

    List<User> findAllByOrderByCriadoEmDesc();

    long countByRole(Role role);

    long countByAtivoTrue();
}
