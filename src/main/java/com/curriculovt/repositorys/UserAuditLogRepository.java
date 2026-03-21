package com.curriculovt.repositorys;

import com.curriculovt.models.UserAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UserAuditLogRepository extends JpaRepository<UserAuditLog, Long> {
    long countByAcao(String acao);
}