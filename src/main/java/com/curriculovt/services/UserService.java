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
        user.setDataCriacaoConta(LocalDateTime.now());

        User saved = userRepository.save(user);
        if (saved.getRole() == UserRole.COMMON) {
            auditRepository.save(new UserAuditLog(saved.getId(), "CRIACAO"));
        }
        return saved;
    }

    @Transactional
    public void adicionarMesDeAssinatura(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNaoEncontradoException("Usuário não encontrado."));

        user.setPagamento(true);

        LocalDateTime novaExpiracao = LocalDateTime.now().plusDays(31);
        user.setDataExpiracao(ajustarParaFinalDoDia(novaExpiracao));

        userRepository.save(user);
    }

    @Transactional
    public void adicionarQuinzeDiasDeAssinatura(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNaoEncontradoException("Usuário não encontrado."));

        user.setPagamento(true);

        LocalDateTime novaExpiracao = LocalDateTime.now().plusDays(15);
        user.setDataExpiracao(ajustarParaFinalDoDia(novaExpiracao));

        userRepository.save(user);
    }

    public Page<User> findAll(String termo, UserRole role, Pageable pageable) {
        validarSuperAdmin();

        if (termo != null && !termo.isBlank() && role != null) {
            return userRepository.findByRoleAndNomeContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
                    role, termo, role, termo, pageable);
        }

        if (termo != null && !termo.isBlank()) {
            return userRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    termo, termo, pageable);
        }

        if (role != null) {
            return userRepository.findByRole(role, pageable);
        }

        return userRepository.findAll(pageable);
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
        }

        if (userLogado.getRole() == UserRole.SUPER_ADMIN) {
            if (userDetails.getRole() != null) {
                userNoBanco.setRole(userDetails.getRole());
            }
            if (userDetails.getDataExpiracao() != null && !userDetails.getDataExpiracao().equals(userNoBanco.getDataExpiracao())) {
                userNoBanco.setDataExpiracao(ajustarParaFinalDoDia(userDetails.getDataExpiracao()));
            }
            if (userDetails.getDataCriacaoConta() != null) {
                userNoBanco.setDataCriacaoConta(userDetails.getDataCriacaoConta());
            }
        }

        return userRepository.save(userNoBanco);
    }

    @Transactional
    public void delete(Long id) {
        validarSuperAdmin();
        User user = findById(id);
        userRepository.delete(user);
    }

    public Map<String, Object> getMetrics(Integer year, Integer month) {
        validarSuperAdmin();

        LocalDateTime agora = LocalDateTime.now();

        long atualizacoesSenha = auditRepository.countByAcao("ATUALIZACAO_SENHA");

        long totalUsuarios = userRepository.countByRole(UserRole.COMMON);
        long totalAdmins = userRepository.countByRole(UserRole.SUPER_ADMIN);

        long totalAtivos = userRepository.countByRoleAndPagamentoTrueAndDataExpiracaoAfter(UserRole.COMMON, agora);

        long totalInativos = userRepository.countByRoleAndPagamentoTrueAndDataExpiracaoBefore(UserRole.COMMON, agora);

        long totalNaoPagos = userRepository.countByRoleAndPagamentoFalse(UserRole.COMMON);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("atualizacoesSenha", atualizacoesSenha);
        metrics.put("totalUsuarios", totalUsuarios);
        metrics.put("totalAdmins", totalAdmins);
        metrics.put("totalAtivos", totalAtivos);
        metrics.put("totalInativos", totalInativos);
        metrics.put("totalNaoPagos", totalNaoPagos);

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