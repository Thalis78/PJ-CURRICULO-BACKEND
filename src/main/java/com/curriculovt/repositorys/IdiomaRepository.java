package com.curriculovt.repositorys;

import com.curriculovt.models.Idioma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdiomaRepository extends JpaRepository<Idioma, Long> {

    List<Idioma> findByProfileId(Long profileId);
}