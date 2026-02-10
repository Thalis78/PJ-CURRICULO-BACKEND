package com.curriculovt.services;

import com.curriculovt.dtos.CurriculoDTO;
import com.curriculovt.exceptions.CurriculoNaoEncontradoException;
import com.curriculovt.models.Curriculo;
import com.curriculovt.repositorys.CurriculoRepository;
import org.springframework.stereotype.Service;

@Service
public class CurriculoService {

    private final CurriculoRepository curriculoRepository;

    public CurriculoService(CurriculoRepository curriculoRepository) {
        this.curriculoRepository = curriculoRepository;
    }

    public Curriculo criar(CurriculoDTO dto) {
        Curriculo curriculo = new Curriculo();
        aplicarDados(dto, curriculo);
        return curriculoRepository.save(curriculo);
    }

    public Curriculo atualizar(Long id, CurriculoDTO dto) {
        Curriculo existente = buscarPorId(id);
        aplicarDados(dto, existente);
        return curriculoRepository.save(existente);
    }

    private void aplicarDados(CurriculoDTO dto, Curriculo curriculo) {
        curriculo.setNome(dto.getNome());
        curriculo.setImg(dto.getImg());
        curriculo.setResumo(dto.getResumo());
        curriculo.setObjetivo(dto.getObjetivo());
        curriculo.setEmail(dto.getEmail());
        curriculo.setTelefone(dto.getTelefone());
    }

    public void excluir(Long id) {
        curriculoRepository.delete(buscarPorId(id));
    }

    public Curriculo buscarPorId(Long id) {
        return curriculoRepository.findById(id)
                .orElseThrow(() ->
                        new CurriculoNaoEncontradoException("Currículo não encontrado"));
    }
}
