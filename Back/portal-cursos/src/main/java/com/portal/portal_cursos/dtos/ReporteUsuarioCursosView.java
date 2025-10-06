package com.portal.portal_cursos.dtos;

public interface ReporteUsuarioCursosView {

    Long getUsuarioId();
    String getUsuario();
    Long getCompletados();
    Long getEnProgreso();
    Long getPendientes();
    String getCursosCompletados();
    String getCursosEnProgreso();
    String getCursosPendientes();
}
