package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.curso.CrearCursoRequest;
import com.portal.portal_cursos.dtos.curso.CursoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ICursoService {
    CursoResponse crearCurso(Long usuarioId, CrearCursoRequest request );
}
