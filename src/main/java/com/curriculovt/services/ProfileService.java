package com.curriculovt.services;

import com.curriculovt.dtos.ProfileDTO;
import com.curriculovt.exceptions.ProfileNaoEncontradoException;
import com.curriculovt.models.Profile;
import com.curriculovt.models.User;
import com.curriculovt.repositorys.ProfileRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;

    public ProfileService(ProfileRepository profileRepository, UserService userService) {
        this.profileRepository = profileRepository;
        this.userService = userService;
    }

    @Transactional
    public Profile criar(ProfileDTO dto) {
        validarPropriedade(dto.getUserId());

        if (profileRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new IllegalStateException("Você já possui um perfil cadastrado. Atualize o existente.");
        }

        Profile profile = new Profile();
        User user = userService.findById(dto.getUserId());
        profile.setUser(user);
        aplicarDados(dto, profile);
        return profileRepository.save(profile);
    }

    @Transactional
    public Profile atualizar(Long id, ProfileDTO dto) {
        Profile existente = profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNaoEncontradoException("Não foi possível atualizar: Perfil não encontrado."));

        validarPropriedade(existente.getUser().getId());

        if (dto.getUserId() != null && !dto.getUserId().equals(existente.getUser().getId())) {
            validarPropriedade(dto.getUserId());
            User novoDono = userService.findById(dto.getUserId());
            existente.setUser(novoDono);
        }

        aplicarDados(dto, existente);
        return profileRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Profile existente = profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNaoEncontradoException("Não foi possível excluir: Perfil não encontrado."));

        validarPropriedade(existente.getUser().getId());

        User dono = existente.getUser();
        dono.setProfile(null);
        profileRepository.delete(existente);
    }
    @Transactional(readOnly = true)
    public Profile buscarPorId(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNaoEncontradoException("Perfil profissional não encontrado"));

        validarPropriedade(profile.getUser().getId());
        return profile;
    }

    @Transactional(readOnly = true)
    public Profile buscarPorUserId(Long userId) {
        validarPropriedade(userId);

        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNaoEncontradoException("Perfil não encontrado para este usuário"));
    }

    private void validarPropriedade(Long donoId) {
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdmin = usuarioLogado.getRole().name().equals("ADMIN");
        boolean isDono = usuarioLogado.getId().equals(donoId);

        if (!isAdmin && !isDono) {
            throw new AccessDeniedException("Acesso negado: Você só pode gerenciar o seu próprio perfil.");
        }
    }

    private void aplicarDados(ProfileDTO dto, Profile profile) {
        profile.setNome(limpar(dto.getNome()));
        profile.setImg(dto.getImg());
        profile.setResumo(limpar(dto.getResumo()));
        profile.setObjetivo(limpar(dto.getObjetivo()));
        profile.setEmail(limpar(dto.getEmail() != null ? dto.getEmail().toLowerCase() : null));
        profile.setTelefone(limpar(dto.getTelefone()));
        profile.setLinkedin(limpar(dto.getLinkedin()));
        profile.setEstado(limpar(dto.getEstado()));
    }

    private String limpar(String texto) {
        return (texto == null) ? null : texto.trim().replaceAll("\\s{2,}", " ");
    }
}