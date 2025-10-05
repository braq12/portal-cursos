package com.portal.portal_cursos.jpa.repository;

import com.portal.portal_cursos.jpa.entity.CapacitacionEntity;
import com.portal.portal_cursos.jpa.entity.ProgresoCapacitacionEntity;
import com.portal.portal_cursos.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgresoCapacitacionRepository extends JpaRepository<ProgresoCapacitacionEntity, Long> {

    Optional<ProgresoCapacitacionEntity> findByUsuarioAndCapacitacion(UsuarioEntity usuario, CapacitacionEntity capacitacion);

    List<ProgresoCapacitacionEntity> findByUsuario(UsuarioEntity usuario);


    List<ProgresoCapacitacionEntity> findByCapacitacion(CapacitacionEntity capacitacion);


    List<ProgresoCapacitacionEntity> findByUsuarioIdAndCapacitacionIdIn(Long usuarioId, List<Long> capacitacionIds);


    Optional<ProgresoCapacitacionEntity> findByUsuarioIdAndCapacitacionId(Long usuarioId, Long capacitacionId);

    int countByUsuarioIdAndCapacitacion_Curso_IdAndEstado(Long usuarioId, Long cursoId, String estado);

}
