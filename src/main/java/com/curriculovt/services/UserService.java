package com.curriculovt.services;

import com.curriculovt.exceptions.UserNaoEncontradoException;
import com.curriculovt.models.User;
import com.curriculovt.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Page<User> findAll(Pageable pageable) {
        validarAdmin();
        return userRepository.findAll(pageable);
    }

    public User findById(Long id) {
        validarPropriedadeOuAdmin(id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNaoEncontradoException("Usuário não encontrado."));
    }

    @Transactional
    public User update(Long id, User userDetails) {
        User userLogado = getUsuarioLogado();
        User userNoBanco = findById(id);

        validarPropriedadeOuAdmin(id);

        if (!userLogado.getRole().name().equals("ADMIN")) {
            userNoBanco.setUsername(userDetails.getUsername());
        } else {
            userNoBanco.setUsername(userDetails.getUsername());
            userNoBanco.setRole(userDetails.getRole());
            userNoBanco.setDataExpiracao(userDetails.getDataExpiracao());
        }

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            userNoBanco.setPassword(passwordEncoder.encode(userDetails.getPassword()));
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

    public List<User> findAll() {
        validarAdmin();
        return userRepository.findAll();
    }

    private User getUsuarioLogado() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void validarAdmin() {
        if (!getUsuarioLogado().getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("Acesso negado: Requer privilégios de administrador.");
        }
    }

    private void validarPropriedadeOuAdmin(Long idAlvo) {
        User logado = getUsuarioLogado();
        if (!logado.getRole().name().equals("ADMIN") && !logado.getId().equals(idAlvo)) {
            throw new AccessDeniedException("Acesso negado: Você não tem permissão para acessar este usuário.");
        }
    }
}