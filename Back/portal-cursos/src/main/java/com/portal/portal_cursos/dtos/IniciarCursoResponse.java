package com.portal.portal_cursos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IniciarCursoResponse {
    private Long cursoId;
    private boolean yaExistia;
    private String estadoCurso;
    private int totalCapacitaciones;
    private List<CapacitacionResumenDto> capacitaciones;
}

