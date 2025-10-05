package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.IniciarCursoResponse;

public interface ICursoProgresoService {

    IniciarCursoResponse iniciarCursoYListarCapacitaciones(Long usuarioId, Long cursoId);


}
