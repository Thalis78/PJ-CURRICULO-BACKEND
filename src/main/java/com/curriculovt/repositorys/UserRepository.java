package com.curriculovt.repositorys;

import com.curriculovt.models.User;
import com.curriculovt.models.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByRole(UserRole role, Pageable pageable);

    Page<User> findByRoleAndUsernameContainingIgnoreCase(UserRole role, String username, Pageable pageable);

    Optional<User> findByUsername(String username);
}