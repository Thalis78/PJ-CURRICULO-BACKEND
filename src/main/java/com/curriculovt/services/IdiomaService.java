package com.curriculovt.services;

import com.curriculovt.dtos.IdiomaDTO;
import com.curriculovt.exceptions.IdiomaNaoEncontradoException;
import com.curriculovt.exceptions.ProfileNaoEncontradoException;
import com.curriculovt.models.Idioma;
import com.curriculovt.models.Profile;
import com.curriculovt.repositorys.IdiomaRepository;
import com.curriculovt.repositorys.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IdiomaService {

    private final IdiomaRepository idiomaRepository;
    private final ProfileRepository profileRepository;

    public IdiomaService(IdiomaRepository idiomaRepository, ProfileRepository profileRepository) {
        this.idiomaRepository = idiomaRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public Idioma criar(Long profileId, IdiomaDTO dto) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNaoEncontradoException("Perfil pai não encontrado"));

        Idioma idioma = new Idioma();
        idioma.setProfile(profile);
        aplicarDados(dto, idioma);

        return idiomaRepository.save(idioma);
    }

    @Transactional
    public Idioma atualizar(Long id, IdiomaDTO dto) {
        Idioma existente = buscarPorId(id);
        aplicarDados(dto, existente);
        return idiomaRepository.save(existente);
    }

    public List<Idioma> listarPorProfile(Long profileId) {
        return idiomaRepository.findByProfileId(profileId);
    }

    public void excluir(Long id) {
        idiomaRepository.delete(buscarPorId(id));
    }

    public Idioma buscarPorId(Long id) {
        return idiomaRepository.findById(id)
                .orElseThrow(() ->
                        new IdiomaNaoEncontradoException("Idioma não encontrado"));
    }

    private void aplicarDados(IdiomaDTO dto, Idioma idioma) {
        idioma.setNome(dto.getNome());
        idioma.setNivel(dto.getNivel());
    }
}