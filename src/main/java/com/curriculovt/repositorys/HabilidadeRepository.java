package com.curriculovt.repositorys;

import com.curriculovt.models.Habilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HabilidadeRepository extends JpaRepository<Habilidade, Long> {
    List<Habilidade> findByProfileId(Long profileId);
}