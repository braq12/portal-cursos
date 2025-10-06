package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.ReporteUsuarioCursoDetalleView;
import com.portal.portal_cursos.dtos.ReporteUsuarioCursosView;

import java.util.List;

public interface IReportesService {


    public List<ReporteUsuarioCursosView> cursosPorUsuario();

    public List<ReporteUsuarioCursoDetalleView> detalleCursosPorUsuario();
}
