package com.curriculovt.services;

import com.curriculovt.dtos.IdiomaDTO;
import com.curriculovt.exceptions.IdiomaNaoEncontradoException;
import com.curriculovt.exceptions.ProfileNaoEncontradoException;
import com.curriculovt.models.Idioma;
import com.curriculovt.models.Profile;
import com.curriculovt.models.User;
import com.curriculovt.repositorys.IdiomaRepository;
import com.curriculovt.repositorys.ProfileRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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
                .orElseThrow(() -> new ProfileNaoEncontradoException("Perfil não encontrado ou excluído"));

        validarPropriedade(profile.getUser().getId());

        Idioma idioma = new Idioma();
        idioma.setProfile(profile);
        aplicarDados(dto, idioma);

        return idiomaRepository.save(idioma);
    }

    @Transactional
    public Idioma atualizar(Long id, IdiomaDTO dto) {
        Idioma existente = buscarPorId(id);
        validarPropriedade(existente.getProfile().getUser().getId());

        aplicarDados(dto, existente);
        return idiomaRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public List<Idioma> listarPorProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNaoEncontradoException("Perfil não encontrado"));

        validarPropriedade(profile.getUser().getId());

        return idiomaRepository.findByProfileId(profileId);
    }

    @Transactional
    public void excluir(Long id) {
        Idioma existente = buscarPorId(id);
        validarPropriedade(existente.getProfile().getUser().getId());
        idiomaRepository.delete(existente);
    }

    @Transactional(readOnly = true)
    public Idioma buscarPorId(Long id) {
        Idioma idioma = idiomaRepository.findById(id)
                .orElseThrow(() ->
                        new IdiomaNaoEncontradoException("Idioma não encontrado"));

        validarPropriedade(idioma.getProfile().getUser().getId());
        return idioma;
    }

    private void validarPropriedade(Long donoId) {
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdmin = usuarioLogado.getRole().name().equals("ADMIN");
        boolean isDono = usuarioLogado.getId().equals(donoId);

        if (!isAdmin && !isDono) {
            throw new AccessDeniedException("Acesso negado: Você não tem permissão para esta ação.");
        }
    }

    private void aplicarDados(IdiomaDTO dto, Idioma idioma) {
        idioma.setNome(limpar(dto.getNome()));
        idioma.setNivel(limpar(dto.getNivel()));
    }

    private String limpar(String texto) {
        return (texto == null) ? null : texto.trim().replaceAll("\\s{2,}", " ");
    }
}