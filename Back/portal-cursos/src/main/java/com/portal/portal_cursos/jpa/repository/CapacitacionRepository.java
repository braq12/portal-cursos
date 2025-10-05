package com.portal.portal_cursos.jpa.repository;

import com.portal.portal_cursos.jpa.entity.CapacitacionEntity;
import com.portal.portal_cursos.jpa.entity.CursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CapacitacionRepository extends JpaRepository<CapacitacionEntity, Long> {


    List<CapacitacionEntity> findByCurso(CursoEntity curso);


    List<CapacitacionEntity> findByCursoOrderByOrdenAsc(CursoEntity curso);

    List<CapacitacionEntity> findByCursoIdOrderByOrdenAsc(Long cursoId);

    int countByCursoId(Long cursoId);
}
