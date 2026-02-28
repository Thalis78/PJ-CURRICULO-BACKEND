package com.curriculovt.services;

import com.curriculovt.dtos.FormacaoDTO;
import com.curriculovt.exceptions.FormacaoNaoEncontradaException;
import com.curriculovt.exceptions.ProfileNaoEncontradoException;
import com.curriculovt.models.Formacao;
import com.curriculovt.models.Profile;
import com.curriculovt.models.User;
import com.curriculovt.repositorys.FormacaoRepository;
import com.curriculovt.repositorys.ProfileRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FormacaoService {

    private final FormacaoRepository formacaoRepository;
    private final ProfileRepository profileRepository;

    public FormacaoService(FormacaoRepository formacaoRepository, ProfileRepository profileRepository) {
        this.formacaoRepository = formacaoRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public Formacao criar(Long profileId, FormacaoDTO dto) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNaoEncontradoException("Perfil pai não encontrado"));

        validarPropriedade(profile.getUser().getId());

        Formacao formacao = new Formacao();
        formacao.setProfile(profile);
        aplicarDados(dto, formacao);

        return formacaoRepository.save(formacao);
    }

    @Transactional
    public Formacao atualizar(Long id, FormacaoDTO dto) {
        Formacao existente = buscarPorId(id);
        validarPropriedade(existente.getProfile().getUser().getId());

        aplicarDados(dto, existente);
        return formacaoRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public List<Formacao> listarPorProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNaoEncontradoException("Perfil não encontrado"));

        validarPropriedade(profile.getUser().getId());

        return formacaoRepository.findByProfileId(profileId);
    }

    @Transactional
    public void excluir(Long id) {
        Formacao existente = buscarPorId(id);
        validarPropriedade(existente.getProfile().getUser().getId());
        formacaoRepository.delete(existente);
    }

    @Transactional(readOnly = true)
    public Formacao buscarPorId(Long id) {
        Formacao formacao = formacaoRepository.findById(id)
                .orElseThrow(() -> new FormacaoNaoEncontradaException("Formação não encontrada"));

        validarPropriedade(formacao.getProfile().getUser().getId());
        return formacao;
    }

    private void validarPropriedade(Long donoId) {
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdmin = usuarioLogado.getRole().name().equals("ADMIN");
        boolean isDono = usuarioLogado.getId().equals(donoId);

        if (!isAdmin && !isDono) {
            throw new AccessDeniedException("Acesso negado: Você não é o dono deste currículo.");
        }
    }

    private void aplicarDados(FormacaoDTO dto, Formacao formacao) {
        formacao.setInstituicao(limpar(dto.getInstituicao()));
        formacao.setCurso(limpar(dto.getCurso()));
        formacao.setTipo(limpar(dto.getTipo()));
        formacao.setDataInicio(dto.getDataInicio());
        formacao.setDataFim(dto.getDataFim());
        formacao.setAtualmente(dto.getAtualmente());
    }

    private String limpar(String texto) {
        if (texto == null) return null;
        return texto.trim().replaceAll("\\s{2,}", " ");
    }
}