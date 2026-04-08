package com.curriculovt.services;

import com.curriculovt.models.Preco;
import com.curriculovt.repositorys.PrecoRepository;
import com.curriculovt.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrecoService {

    private final PrecoRepository precoRepository;

    public PrecoService(PrecoRepository precoRepository) {
        this.precoRepository = precoRepository;
    }

    @Transactional(readOnly = true)
    public Preco buscarConfiguracao() {
        return precoRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Configuração de preço não encontrada."));
    }

    @Transactional
    public Preco criarOuAtualizar(Preco dados) {
        return precoRepository.findAll().stream()
                .findFirst()
                .map(existente -> {
                    existente.setValorBase(dados.getValorBase());
                    existente.setPercentualDesconto(dados.getPercentualDesconto());
                    return precoRepository.save(existente);
                })
                .orElseGet(() -> precoRepository.save(dados));
    }

    @Transactional
    public Preco editar(Preco dados) {
        Preco existente = buscarConfiguracao();
        existente.setValorBase(dados.getValorBase());
        existente.setPercentualDesconto(dados.getPercentualDesconto());
        return precoRepository.save(existente);
    }
}