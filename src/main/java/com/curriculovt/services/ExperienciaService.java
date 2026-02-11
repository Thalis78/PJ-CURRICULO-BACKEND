package com.curriculovt.services;

import com.curriculovt.dtos.ExperienciaDTO;
import com.curriculovt.exceptions.ExperienciaNaoEncontradaException;
import com.curriculovt.models.Curriculo;
import com.curriculovt.models.Experiencia;
import com.curriculovt.repositorys.CurriculoRepository;
import com.curriculovt.repositorys.ExperienciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExperienciaService {

    private final ExperienciaRepository experienciaRepository;
    private final CurriculoRepository curriculoRepository;

    public ExperienciaService(ExperienciaRepository experienciaRepository, CurriculoRepository curriculoRepository) {
        this.experienciaRepository = experienciaRepository;
        this.curriculoRepository = curriculoRepository;
    }

    @Transactional
    public Experiencia criar(Long curriculoId, ExperienciaDTO dto) {
        Curriculo curriculo = curriculoRepository.findById(curriculoId)
                .orElseThrow(() -> new RuntimeException("Currículo pai não encontrado"));

        Experiencia experiencia = new Experiencia();
        experiencia.setCurriculo(curriculo);
        aplicarDados(dto, experiencia);

        return experienciaRepository.save(experiencia);
    }

    @Transactional
    public Experiencia atualizar(Long id, ExperienciaDTO dto) {
        Experiencia existente = buscarPorId(id);
        aplicarDados(dto, existente);
        return experienciaRepository.save(existente);
    }

    public List<Experiencia> listarPorCurriculo(Long curriculoId) {
        return experienciaRepository.findByCurriculoId(curriculoId);
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
        experiencia.setEmpresa(dto.getEmpresa());
        experiencia.setCargo(dto.getCargo());
        experiencia.setDataInicio(dto.getDataInicio());
        experiencia.setDataFim(dto.getDataFim());
        experiencia.setAtual(dto.isAtual());
        experiencia.setDescricao(dto.getDescricao());
    }
}