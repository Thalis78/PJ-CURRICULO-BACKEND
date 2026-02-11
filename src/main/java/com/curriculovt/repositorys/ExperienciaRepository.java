package com.curriculovt.repositorys;

import com.curriculovt.models.Experiencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExperienciaRepository extends JpaRepository<Experiencia, Long> {

    List<Experiencia> findByCurriculoId(Long curriculoId);
}