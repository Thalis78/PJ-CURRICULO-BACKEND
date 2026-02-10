package com.curriculovt.repositorys;

import com.curriculovt.models.Curriculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurriculoRepository extends JpaRepository<Curriculo, Long> {
}
