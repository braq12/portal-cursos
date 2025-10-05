package com.portal.portal_cursos.jpa.repository;

import com.portal.portal_cursos.jpa.entity.InsigniaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsigniaRepository extends JpaRepository<InsigniaEntity, Long> {

    boolean existsByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);

}

