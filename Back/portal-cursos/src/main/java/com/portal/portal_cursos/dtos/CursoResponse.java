package com.portal.portal_cursos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CursoResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private Instant fechaCreacion;
}
