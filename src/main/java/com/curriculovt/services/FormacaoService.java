package com.curriculovt.services;

import com.curriculovt.dtos.FormacaoDTO;
import com.curriculovt.models.Formacao;
import com.curriculovt.models.Profile;
import com.curriculovt.repositorys.FormacaoRepository;
import com.curriculovt.repositorys.ProfileRepository;
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
                .orElseThrow(() -> new RuntimeException("Perfil pai não encontrado"));

        Formacao formacao = new Formacao();
        formacao.setProfile(profile);
        aplicarDados(dto, formacao);

        return formacaoRepository.save(formacao);
    }

    @Transactional
    public Formacao atualizar(Long id, FormacaoDTO dto) {
        Formacao existente = buscarPorId(id);
        aplicarDados(dto, existente);
        return formacaoRepository.save(existente);
    }

    public List<Formacao> listarPorProfile(Long profileId) {
        return formacaoRepository.findByProfileId(profileId);
    }

    public void excluir(Long id) {
        formacaoRepository.delete(buscarPorId(id));
    }

    public Formacao buscarPorId(Long id) {
        return formacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formação não encontrada"));
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