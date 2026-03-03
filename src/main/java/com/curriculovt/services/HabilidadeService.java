package com.curriculovt.services;

import com.curriculovt.dtos.HabilidadeDTO;
import com.curriculovt.exceptions.HabilidadeNaoEncontradaException;
import com.curriculovt.exceptions.ProfileNaoEncontradoException;
import com.curriculovt.models.Habilidade;
import com.curriculovt.models.Profile;
import com.curriculovt.models.User;
import com.curriculovt.repositorys.HabilidadeRepository;
import com.curriculovt.repositorys.ProfileRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HabilidadeService {

    private final HabilidadeRepository habilidadeRepository;
    private final ProfileRepository profileRepository;

    public HabilidadeService(HabilidadeRepository habilidadeRepository, ProfileRepository profileRepository) {
        this.habilidadeRepository = habilidadeRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public Habilidade criar(Long profileId, HabilidadeDTO dto) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNaoEncontradoException("Perfil não encontrado ou excluído"));

        validarPropriedade(profile.getUser().getId());

        Habilidade habilidade = new Habilidade();
        habilidade.setProfile(profile);
        aplicarDados(dto, habilidade);

        return habilidadeRepository.save(habilidade);
    }

    @Transactional
    public Habilidade atualizar(Long id, HabilidadeDTO dto) {
        Habilidade existente = buscarPorId(id);
        validarPropriedade(existente.getProfile().getUser().getId());

        aplicarDados(dto, existente);
        return habilidadeRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public List<Habilidade> listarPorProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNaoEncontradoException("Perfil não encontrado"));

        validarPropriedade(profile.getUser().getId());

        return habilidadeRepository.findByProfileId(profileId);
    }

    @Transactional
    public void excluir(Long id) {
        Habilidade existente = buscarPorId(id);
        validarPropriedade(existente.getProfile().getUser().getId());
        habilidadeRepository.delete(existente);
    }

    @Transactional(readOnly = true)
    public Habilidade buscarPorId(Long id) {
        Habilidade habilidade = habilidadeRepository.findById(id)
                .orElseThrow(() ->
                        new HabilidadeNaoEncontradaException("Habilidade não encontrada"));

        validarPropriedade(habilidade.getProfile().getUser().getId());
        return habilidade;
    }

    private void validarPropriedade(Long donoId) {
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdmin = usuarioLogado.getRole().name().equals("ADMIN");
        boolean isDono = usuarioLogado.getId().equals(donoId);

        if (!isAdmin && !isDono) {
            throw new AccessDeniedException("Acesso negado: Você não é o dono deste currículo.");
        }
    }

    private void aplicarDados(HabilidadeDTO dto, Habilidade habilidade) {
        habilidade.setNome(limpar(dto.getNome()));
    }

    private String limpar(String texto) {
        return (texto == null) ? null : texto.trim().replaceAll("\\s{2,}", " ");
    }
}