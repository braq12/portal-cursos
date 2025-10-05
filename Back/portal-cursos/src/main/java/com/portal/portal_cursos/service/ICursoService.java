package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.CrearCursoRequest;
import com.portal.portal_cursos.dtos.CursoListadoItemDto;
import com.portal.portal_cursos.dtos.CursoResponse;

import java.util.List;

public interface ICursoService {
    CursoResponse crearCurso(Long usuarioId, CrearCursoRequest request);
}
