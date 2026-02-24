package com.curriculovt.services;

import com.curriculovt.dtos.ProfileDTO;
import com.curriculovt.exceptions.ProfileNaoEncontradoException;
import com.curriculovt.models.Profile;
import com.curriculovt.repositorys.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional
    public Profile criar(ProfileDTO dto) {
        Profile profile = new Profile();
        aplicarDados(dto, profile);
        return profileRepository.save(profile);
    }

    @Transactional
    public Profile atualizar(Long id, ProfileDTO dto) {
        Profile existente = buscarPorId(id);
        aplicarDados(dto, existente);
        return profileRepository.save(existente);
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

    @Transactional
    public void excluir(Long id) {
        profileRepository.delete(buscarPorId(id));
    }

    public Profile buscarPorId(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() ->
                        new ProfileNaoEncontradoException("Perfil profissional não encontrado"));
    }
}