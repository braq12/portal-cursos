package com.portal.portal_cursos.dtos;

public interface ReporteUsuarioCursoDetalleView {
    Long getUsuarioId();
    String getUsuario();
    Long getCursoId();
    String getCurso();
    String getEstado();
    java.time.LocalDateTime getFechaInicio();
    java.time.LocalDateTime getFechaCompletado();
}
