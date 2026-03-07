package com.curriculovt.repositorys;

import com.curriculovt.models.User;
import com.curriculovt.models.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Page<User> findByRole(UserRole role, Pageable pageable);

    Page<User> findByRoleAndNomeContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
            UserRole role1, String nome, UserRole role2, String email, Pageable pageable);

    long countByRoleAndDataExpiracaoAfter(UserRole role, LocalDateTime agora);

    long countByRoleAndDataExpiracaoBetween(UserRole role, LocalDateTime inicio, LocalDateTime fim);
}