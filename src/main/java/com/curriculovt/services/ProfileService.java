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
        profile.setNome(dto.getNome());
        profile.setImg(dto.getImg());
        profile.setResumo(dto.getResumo());
        profile.setObjetivo(dto.getObjetivo());
        profile.setEmail(dto.getEmail());
        profile.setTelefone(dto.getTelefone());
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