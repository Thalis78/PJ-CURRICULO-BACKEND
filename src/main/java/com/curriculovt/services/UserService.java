package com.curriculovt.services;

import com.curriculovt.exceptions.UserNaoEncontradoException;
import com.curriculovt.models.User;
import com.curriculovt.models.UserRole;
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
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private void validarSenhaForte(String password) {
        if (password == null || !password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres, incluir uma letra maiúscula e um número.");
        }
    }

    @Transactional
    public User saveUser(User user) {
        validarSenhaForte(user.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getUsername() != null) {
            user.setUsername(user.getUsername().toLowerCase().trim());
        }

        if (user.getRole() == UserRole.COMMON) {
            user.setDataExpiracao(LocalDateTime.now().plusMonths(1));
        } else {
            user.setDataExpiracao(null);
        }

        return userRepository.save(user);
    }

    public Page<User> findAllCommon(String username, Pageable pageable) {
        validarAdmin();
        if (username != null && !username.isBlank()) {
            return userRepository.findByRoleAndUsernameContainingIgnoreCase(UserRole.COMMON, username, pageable);
        }
        return userRepository.findByRole(UserRole.COMMON, pageable);
    }

    public User findById(Long id) {
        validarPropriedadeOuAdmin(id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNaoEncontradoException("Usuário não encontrado."));
    }

    @Transactional
    public User update(Long id, User userDetails) {
        User userLogado = getUsuarioLogado();
        User userNoBanco = userRepository.findById(id)
                .orElseThrow(() -> new UserNaoEncontradoException("Usuário não encontrado."));

        validarPropriedadeOuAdmin(id);

        String senhaVindaDoFront = userDetails.getPassword();
        String senhaAtualNoBanco = userNoBanco.getPassword();

        if (senhaVindaDoFront != null && !senhaVindaDoFront.isBlank()) {
            if (!senhaVindaDoFront.equals(senhaAtualNoBanco) && !senhaVindaDoFront.startsWith("$2a$")) {
                validarSenhaForte(senhaVindaDoFront);
                userNoBanco.setPassword(passwordEncoder.encode(senhaVindaDoFront));
            }
        }

        if (userDetails.getUsername() != null) {
            userNoBanco.setUsername(userDetails.getUsername().toLowerCase().trim());
        }

        if (userLogado.getRole() == UserRole.ADMIN) {
            userNoBanco.setRole(userDetails.getRole());
            userNoBanco.setDataExpiracao(userDetails.getDataExpiracao());
        }

        return userRepository.save(userNoBanco);
    }

    @Transactional
    public void delete(Long id) {
        validarAdmin();
        User user = findById(id);
        userRepository.delete(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private User getUsuarioLogado() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void validarAdmin() {
        if (getUsuarioLogado().getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Acesso negado: Requer privilégios de administrador.");
        }
    }

    private void validarPropriedadeOuAdmin(Long idAlvo) {
        User logado = getUsuarioLogado();
        if (logado.getRole() != UserRole.ADMIN && !logado.getId().equals(idAlvo)) {
            throw new AccessDeniedException("Acesso negado: Sem permissão para este usuário.");
        }
    }
}