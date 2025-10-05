package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.CursoListadoItemDto;
import com.portal.portal_cursos.dtos.ListarCursosResponse;

import java.util.List;

public interface ICursoQueryService {

    List<CursoListadoItemDto> listarCursosDisponiblesParaUsuario(Long usuarioId);

    List<ListarCursosResponse> listarCursos();
}
