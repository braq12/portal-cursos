package com.portal.portal_cursos.jpa.repository;


import com.portal.portal_cursos.jpa.entity.ProgresoCursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgresoCursoRepository extends JpaRepository<ProgresoCursoEntity, Long> {
    Optional<ProgresoCursoEntity> findByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);

    List<ProgresoCursoEntity> findByUsuarioIdAndCursoIdIn(Long usuarioId, List<Long> cursoIds);

}

