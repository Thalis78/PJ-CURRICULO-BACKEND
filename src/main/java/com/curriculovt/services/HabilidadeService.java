package com.curriculovt.services;

import com.curriculovt.dtos.HabilidadeDTO;
import com.curriculovt.exceptions.HabilidadeNaoEncontradaException;
import com.curriculovt.exceptions.ProfileNaoEncontradoException;
import com.curriculovt.models.Habilidade;
import com.curriculovt.models.Profile;
import com.curriculovt.repositorys.HabilidadeRepository;
import com.curriculovt.repositorys.ProfileRepository;
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
                .orElseThrow(() -> new ProfileNaoEncontradoException("Perfil pai não encontrado"));

        Habilidade habilidade = new Habilidade();
        habilidade.setProfile(profile);
        aplicarDados(dto, habilidade);

        return habilidadeRepository.save(habilidade);
    }

    @Transactional
    public Habilidade atualizar(Long id, HabilidadeDTO dto) {
        Habilidade existente = buscarPorId(id);
        aplicarDados(dto, existente);
        return habilidadeRepository.save(existente);
    }

    public List<Habilidade> listarPorProfile(Long profileId) {
        return habilidadeRepository.findByProfileId(profileId);
    }

    public void excluir(Long id) {
        habilidadeRepository.delete(buscarPorId(id));
    }

    public Habilidade buscarPorId(Long id) {
        return habilidadeRepository.findById(id)
                .orElseThrow(() ->
                        new HabilidadeNaoEncontradaException("Habilidade não encontrada"));
    }

    private void aplicarDados(HabilidadeDTO dto, Habilidade habilidade) {
        habilidade.setNome(limpar(dto.getNome()));
    }

    private String limpar(String texto) {
        return (texto == null) ? null : texto.trim().replaceAll("\\s{2,}", " ");
    }
}