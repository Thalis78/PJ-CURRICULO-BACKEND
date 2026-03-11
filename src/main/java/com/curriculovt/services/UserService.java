package com.curriculovt.services;

import com.curriculovt.exceptions.UserNaoEncontradoException;
import com.curriculovt.models.User;
import com.curriculovt.models.UserAuditLog;
import com.curriculovt.models.UserRole;
import com.curriculovt.repositorys.UserAuditLogRepository;
import com.curriculovt.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserAuditLogRepository auditRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private void validarSenhaForte(String password) {
        if (password == null || !password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres, incluir uma letra maiúscula e um número.");
        }
    }

    private LocalDateTime ajustarParaFinalDoDia(LocalDateTime data) {
        if (data == null) return null;
        return data.withHour(23).withMinute(59).withSecond(59).withNano(0);
    }

    @Transactional
    public User saveUser(User user) {
        validarSenhaForte(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setSenhaRedefinidaPorEmail(false);

        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().toLowerCase().trim());
        }

        user.setPagamento(false);
        user.setDataExpiracao(null);

        User saved = userRepository.save(user);
        auditRepository.save(new UserAuditLog(saved.getId(), "CRIACAO"));
        return saved;
    }

    @Transactional
    public void adicionarMesDeAssinatura(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNaoEncontradoException("Usuário não encontrado."));

        user.setPagamento(true);
        LocalDateTime novaExpiracao = LocalDateTime.now().plusMonths(1);
        user.setDataExpiracao(ajustarParaFinalDoDia(novaExpiracao));

        userRepository.save(user);
        auditRepository.save(new UserAuditLog(user.getId(), "ATUALIZACAO_DATA"));
    }

    public Page<User> findAllCommon(String termo, Pageable pageable) {
        validarSuperAdmin();
        if (termo != null && !termo.isBlank()) {
            return userRepository.findByRoleAndNomeContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
                    UserRole.COMMON, termo, UserRole.COMMON, termo, pageable);
        }
        return userRepository.findByRole(UserRole.COMMON, pageable);
    }

    public User findById(Long id) {
        validarPropriedadeOuSuperAdmin(id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNaoEncontradoException("Usuário não encontrado."));
    }

    @Transactional
    public User update(Long id, User userDetails) {
        User userLogado = getUsuarioLogado();
        User userNoBanco = userRepository.findById(id)
                .orElseThrow(() -> new UserNaoEncontradoException("Usuário não encontrado."));

        validarPropriedadeOuSuperAdmin(id);

        if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
            if (!passwordEncoder.matches(userDetails.getPassword(), userNoBanco.getPassword()) && !userDetails.getPassword().startsWith("$2a$")) {
                validarSenhaForte(userDetails.getPassword());
                userNoBanco.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                userNoBanco.setSenhaRedefinidaPorEmail(false);
                auditRepository.save(new UserAuditLog(id, "ATUALIZACAO_SENHA"));
            }
        }

        if (userDetails.getNome() != null && !userDetails.getNome().equals(userNoBanco.getNome())) {
            userNoBanco.setNome(userDetails.getNome());
        }

        if (userDetails.getEmail() != null && !userDetails.getEmail().equalsIgnoreCase(userNoBanco.getEmail())) {
            userNoBanco.setEmail(userDetails.getEmail().toLowerCase().trim());
            auditRepository.save(new UserAuditLog(id, "ATUALIZACAO_EMAIL"));
        }

        if (userLogado.getRole() == UserRole.SUPER_ADMIN) {
            if (userDetails.getRole() != null) {
                userNoBanco.setRole(userDetails.getRole());
            }
            if (userDetails.getDataExpiracao() != null && !userDetails.getDataExpiracao().equals(userNoBanco.getDataExpiracao())) {
                userNoBanco.setDataExpiracao(ajustarParaFinalDoDia(userDetails.getDataExpiracao()));
                auditRepository.save(new UserAuditLog(id, "ATUALIZACAO_DATA"));
            }
        }

        return userRepository.save(userNoBanco);
    }

    @Transactional
    public void delete(Long id) {
        validarSuperAdmin();
        User user = findById(id);
        userRepository.delete(user);
        auditRepository.save(new UserAuditLog(id, "EXCLUSAO"));
    }

    public Map<String, Long> getMetrics(Integer year, Integer month) {
        validarSuperAdmin();

        LocalDateTime inicio;
        LocalDateTime fim;
        LocalDateTime agora = LocalDateTime.now();

        if (year != null && month != null) {
            inicio = LocalDateTime.of(year, month, 3, 0, 0, 0);
            fim = inicio.plusMonths(1);
        } else {
            if (agora.getDayOfMonth() >= 3) {
                inicio = agora.withDayOfMonth(3).withHour(0).withMinute(0).withSecond(0).withNano(0);
                fim = inicio.plusMonths(1);
            } else {
                inicio = agora.minusMonths(1).withDayOfMonth(3).withHour(0).withMinute(0).withSecond(0).withNano(0);
                fim = inicio.plusMonths(1);
            }
        }

        Map<String, Long> metrics = new HashMap<>();

        metrics.put("criados", auditRepository.countByAcaoAndDataEventoBetween("CRIACAO", inicio, fim));
        metrics.put("atualizacoesSenha", auditRepository.countByAcaoAndDataEventoBetween("ATUALIZACAO_SENHA", inicio, fim));
        metrics.put("atualizacoesEmail", auditRepository.countByAcaoAndDataEventoBetween("ATUALIZACAO_EMAIL", inicio, fim));
        metrics.put("atualizacoesData", auditRepository.countByAcaoAndDataEventoBetween("ATUALIZACAO_DATA", inicio, fim));
        metrics.put("excluidos", auditRepository.countByAcaoAndDataEventoBetween("EXCLUSAO", inicio, fim));

        metrics.put("totalAtivos", userRepository.countByRoleAndDataExpiracaoAfter(UserRole.COMMON, agora));

        metrics.put("expirados", userRepository.countByRoleAndDataExpiracaoBetween(UserRole.COMMON, inicio, fim));

        return metrics;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private User getUsuarioLogado() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void validarSuperAdmin() {
        UserRole role = getUsuarioLogado().getRole();
        if (role != UserRole.SUPER_ADMIN) {
            throw new AccessDeniedException("Acesso negado: Requer privilégios de Super Admin.");
        }
    }

    private void validarPropriedadeOuSuperAdmin(Long idAlvo) {
        User logado = getUsuarioLogado();
        boolean isSuperAdmin = logado.getRole() == UserRole.SUPER_ADMIN;
        if (!isSuperAdmin && !logado.getId().equals(idAlvo)) {
            throw new AccessDeniedException("Acesso negado: Sem permissão para este usuário.");
        }
    }
}