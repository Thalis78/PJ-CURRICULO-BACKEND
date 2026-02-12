package com.curriculovt.repositorys;

import com.curriculovt.models.Formacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormacaoRepository extends JpaRepository<Formacao, Long> {
    List<Formacao> findByProfileId(Long profileId);

}
