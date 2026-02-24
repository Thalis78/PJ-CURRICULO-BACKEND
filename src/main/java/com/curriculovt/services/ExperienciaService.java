package com.curriculovt.services;

import com.curriculovt.dtos.ExperienciaDTO;
import com.curriculovt.exceptions.ExperienciaNaoEncontradaException;
import com.curriculovt.models.Profile;
import com.curriculovt.models.Experiencia;
import com.curriculovt.repositorys.ProfileRepository;
import com.curriculovt.repositorys.ExperienciaRepository;
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
                .orElseThrow(() -> new RuntimeException("Perfil pai não encontrado"));

        Experiencia experiencia = new Experiencia();
        experiencia.setProfile(profile);
        aplicarDados(dto, experiencia);

        return experienciaRepository.save(experiencia);
    }

    @Transactional
    public Experiencia atualizar(Long id, ExperienciaDTO dto) {
        Experiencia existente = buscarPorId(id);
        aplicarDados(dto, existente);
        return experienciaRepository.save(existente);
    }

    public List<Experiencia> listarPorProfile(Long profileId) {
        return experienciaRepository.findByProfileId(profileId);
    }

    public void excluir(Long id) {
        experienciaRepository.delete(buscarPorId(id));
    }

    public Experiencia buscarPorId(Long id) {
        return experienciaRepository.findById(id)
                .orElseThrow(() ->
                        new ExperienciaNaoEncontradaException("Experiência profissional não encontrada"));
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
        if (texto == null) {
            return null;
        }
        return texto.trim().replaceAll("\\s{2,}", " ");
    }
}