package com.portal.portal_cursos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IniciarCapacitacionResponse {
    private Long cursoId;
    private Long capacitacionId;
    private String titulo;
    private String tipo;
    private String estado;
    private String url;
    private int expiraSegundos;
}

