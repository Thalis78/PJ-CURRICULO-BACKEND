package com.curriculovt.services;

import com.curriculovt.dtos.ExperienciaDTO;
import com.curriculovt.exceptions.ExperienciaNaoEncontradaException;
import com.curriculovt.exceptions.ResourceNotFoundException;
import com.curriculovt.models.Profile;
import com.curriculovt.models.Experiencia;
import com.curriculovt.models.User;
import com.curriculovt.repositorys.ProfileRepository;
import com.curriculovt.repositorys.ExperienciaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExperienciaService {

    private final ExperienciaRepository experienciaRepository;
    private final ProfileRepository profileRepository;

    public ExperienciaService(ExperienciaRepository experienciaRepository, ProfileRepository profileRepository) {
        this.experienciaRepository = experienciaRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public Experiencia criar(Long profileId, ExperienciaDTO dto) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado ou excluído"));

        validarPropriedade(profile.getUser().getId());

        Experiencia experiencia = new Experiencia();
        experiencia.setProfile(profile);
        aplicarDados(dto, experiencia);

        return experienciaRepository.save(experiencia);
    }

    @Transactional
    public Experiencia atualizar(Long id, ExperienciaDTO dto) {
        Experiencia existente = buscarPorId(id);
        validarPropriedade(existente.getProfile().getUser().getId());

        aplicarDados(dto, existente);
        return experienciaRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public List<Experiencia> listarPorProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado ou excluído"));

        validarPropriedade(profile.getUser().getId());

        return experienciaRepository.findByProfileIdOrderByDataInicioDesc(profileId);
    }

    @Transactional
    public void excluir(Long id) {
        Experiencia existente = buscarPorId(id);
        validarPropriedade(existente.getProfile().getUser().getId());
        experienciaRepository.delete(existente);
    }

    @Transactional(readOnly = true)
    public Experiencia buscarPorId(Long id) {
        Experiencia experiencia = experienciaRepository.findById(id)
                .orElseThrow(() ->
                        new ExperienciaNaoEncontradaException("Experiência profissional não encontrada"));

        validarPropriedade(experiencia.getProfile().getUser().getId());
        return experiencia;
    }

    private void validarPropriedade(Long donoId) {
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdmin = usuarioLogado.getRole().name().equals("ADMIN");
        boolean isDono = usuarioLogado.getId().equals(donoId);

        if (!isAdmin && !isDono) {
            throw new AccessDeniedException("Acesso negado: Você não é o dono deste currículo.");
        }
    }

    private void aplicarDados(ExperienciaDTO dto, Experiencia experiencia) {
        experiencia.setEmpresa(limparTexto(dto.getEmpresa()));
        experiencia.setCargo(limparTexto(dto.getCargo()));
        experiencia.setDataInicio(dto.getDataInicio());
        experiencia.setDataFim(dto.getDataFim());
        experiencia.setAtualmente(dto.getAtualmente());
        experiencia.setDescricao(dto.getDescricao() != null ? dto.getDescricao().trim() : null);
    }

    private String limparTexto(String texto) {
        return (texto == null) ? null : texto.trim().replaceAll("\\s{2,}", " ");
    }
}