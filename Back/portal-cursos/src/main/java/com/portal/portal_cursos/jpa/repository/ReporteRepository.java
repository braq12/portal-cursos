package com.portal.portal_cursos.jpa.repository;

import com.portal.portal_cursos.dtos.ReporteUsuarioCursoDetalleView;
import com.portal.portal_cursos.dtos.ReporteUsuarioCursosView;
import com.portal.portal_cursos.jpa.entity.ProgresoCursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReporteRepository extends JpaRepository<ProgresoCursoEntity, Long> {

    @Query(value = "SELECT * FROM vw_reporte_usuario_cursos", nativeQuery = true)
    List<ReporteUsuarioCursosView> resumenPorUsuario();

    @Query(value = """
                SELECT * FROM vw_reporte_usuario_curso_detalle
                ORDER BY usuario, curso
            """, nativeQuery = true)
    List<ReporteUsuarioCursoDetalleView> detallePorUsuario();
}
