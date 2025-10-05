package com.portal.portal_cursos.dtos;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacitacionResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private String tipo;
    private String url;
    private Integer duracionMinutos;
    private Integer orden;
    private Instant fechaCreacion;
    private Long cursoId;
}

